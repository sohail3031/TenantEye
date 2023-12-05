package com.example.tenanteye.tenanthomepages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.tenanteye.Post;
import com.example.tenanteye.R;
import com.example.tenanteye.freelancerhomepages.FreelancerMoreActivity;
import com.example.tenanteye.freelancerhomepages.FreelancerTaskActivity;
import com.example.tenanteye.freelancerhomepages.FreelancerUpdateProfilePictureActivity;
import com.example.tenanteye.freelancerhomepages.FreelancerUploadEvidenceActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class TenantCheckEvidenceActivity extends AppCompatActivity {
    private final ArrayList<SlideModel> slideModels = new ArrayList<>();
    private ImageView backImageView;
    private ImageSlider imageSlider;
    private AppCompatButton acceptButton, rejectButton;
    private Post post;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_check_evidence);

        initializeAllVariables();
        getDataFromPreviousActivity();
        getImagesFromFirebase();

        new Handler().postDelayed(() -> {
            backImageView.setOnClickListener(view -> showAlertMessage());
            acceptButton.setOnClickListener(view -> acceptFreelancerWork());
            rejectButton.setOnClickListener(view -> rejectFreelancerWork());
        }, 500);
    }

    private void rejectFreelancerWork() {
        String[] splitEmailAddress = post.getAssignedBy().split("\\.");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    if (post.getAssignedBy().equalsIgnoreCase(Objects.requireNonNull(dataSnapshot.child("assignedBy").getValue()).toString()) && post.getUniqueIdentifier().equalsIgnoreCase(Objects.requireNonNull(dataSnapshot.child("uniqueIdentifier").getValue()).toString())) {
                        post.setStatus("Assigned");
                        post.setFreelancerAcceptedTask(false);

                        String key = Objects.requireNonNull(dataSnapshot.getKey());

                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("status").setValue(post.getStatus());
                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("freelancerAcceptedTask").setValue(post.isFreelancerAcceptedTask());

                        Toast.makeText(TenantCheckEvidenceActivity.this, "Task Rejected!", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(TenantCheckEvidenceActivity.this, TenantTaskActivity.class));
                        finish();
                    }
                }

                imageSlider.setImageList(slideModels, ScaleTypes.FIT);
            } else {
                showSomethingWentWrongError();
            }
        });
    }

    private void acceptFreelancerWork() {
        String[] splitEmailAddress = post.getAssignedBy().split("\\.");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    if (post.getAssignedBy().equalsIgnoreCase(Objects.requireNonNull(dataSnapshot.child("assignedBy").getValue()).toString()) && post.getUniqueIdentifier().equalsIgnoreCase(Objects.requireNonNull(dataSnapshot.child("uniqueIdentifier").getValue()).toString())) {
                        post.setStatus("Closed");

                        String key = Objects.requireNonNull(dataSnapshot.getKey());

                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("status").setValue(post.getStatus());

                        Toast.makeText(TenantCheckEvidenceActivity.this, "Task Closed!", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(TenantCheckEvidenceActivity.this, TenantTaskActivity.class));
                        finish();
                    }
                }

                imageSlider.setImageList(slideModels, ScaleTypes.FIT);
            } else {
                showSomethingWentWrongError();
            }
        });
    }

    private void showAlertMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle(R.string.sign_up_alert_title)
                .setMessage(R.string.sign_up_alert_message)
                .setPositiveButton(R.string.alert_yes, (dialog, which) -> {
                    startActivity(new Intent(this, FreelancerTaskActivity.class));
                    finish();
                })
                .setNegativeButton(R.string.alert_no, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void getImagesFromFirebase() {
        String[] splitEmailAddress = post.getAssignedBy().split("\\.");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    if (post.getAssignedBy().equalsIgnoreCase(Objects.requireNonNull(dataSnapshot.child("assignedBy").getValue()).toString()) && post.getUniqueIdentifier().equalsIgnoreCase(Objects.requireNonNull(dataSnapshot.child("uniqueIdentifier").getValue()).toString())) {
                        int index = 0;

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.child("evidences").getChildren()) {
                            slideModels.add(new SlideModel(Objects.requireNonNull(dataSnapshot1.getValue()).toString(), ScaleTypes.FIT));
                        }
                    }
                }

                imageSlider.setImageList(slideModels, ScaleTypes.FIT);
            } else {
                showSomethingWentWrongError();
            }
        });
    }

    private void showSomethingWentWrongError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle("Something went wrong")
                .setMessage("Please try again")
                .setPositiveButton(R.string.error_alert_okay, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void getDataFromPreviousActivity() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        post = (Post) bundle.getSerializable("post");
    }

    private void initializeAllVariables() {
        backImageView = findViewById(R.id.tenant_check_evidence_back_arrow_image);
        imageSlider = findViewById(R.id.tenant_check_evidence_image_slider);
        acceptButton = findViewById(R.id.tenant_check_evidence_accept_button);
        rejectButton = findViewById(R.id.tenant_check_evidence_reject_button);
    }
}