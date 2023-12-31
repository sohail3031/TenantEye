package com.example.tenanteye.freelancerhomepages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tenanteye.Post;
import com.example.tenanteye.R;
import com.example.tenanteye.login.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class FreelancerSelectedPostActivity extends AppCompatActivity {
    private Post post = new Post();
    private EditText countrySpinner, stateSpinner, citySpinner, endTime, endDate, title, description, address, zipCode, startDate, startTime, link, assignedBy, amount;
    private AppCompatButton acceptTaskButton, addProofButton;
    private ImageView backImageView;
    private String emailAddress;
    private boolean isButtonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer_selected_post);

        SharedPreferences loginSharedPreference = getSharedPreferences("login", Context.MODE_PRIVATE);
        emailAddress = loginSharedPreference.getString("emailAddress", "");

        if (emailAddress.equals("")) {
            Toast.makeText(this, "Please login in again!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        post = (Post) bundle.getSerializable("post");
        assert post != null;

        initializeAllVariables();
        addDataToFields();

        if (!post.isFreelancerAcceptedTask() || post.getStatus().equalsIgnoreCase("assigned")) {
            acceptTaskButton.setVisibility(View.VISIBLE);
            addProofButton.setVisibility(View.GONE);
        } else if (post.getStatus().equalsIgnoreCase("completed") || post.getStatus().equalsIgnoreCase("closed")) {
            acceptTaskButton.setVisibility(View.GONE);
            addProofButton.setVisibility(View.GONE);
        } else {
            acceptTaskButton.setVisibility(View.GONE);
            addProofButton.setVisibility(View.VISIBLE);
        }

        acceptTaskButton.setOnClickListener(view -> {
            if (!isButtonClicked) {
                freelancerAcceptedTheTask();
            } else {
                Toast.makeText(this, "Please wait!", Toast.LENGTH_SHORT).show();
            }
        });

        addProofButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, FreelancerAddEvidenceOptionActivity.class);

            intent.putExtra("post", post);

            startActivity(intent);
            finish();
        });

        backImageView.setOnClickListener(view -> showAlertMessage());
    }

    private void showAlertMessage() {
        if (!emailAddress.isEmpty()) {
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
        } else {
            finish();
        }
    }

    private void freelancerAcceptedTheTask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle("Alert!")
                .setMessage("You are about to pay " + post.getAmount() + " to " + post.getAssignedBy())
                .setPositiveButton(R.string.alert_yes, (dialog, which) -> {
                    Toast.makeText(this, "Please wait while we process the payment", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> {
                        String[] splitEmailAddress = post.getAssignedBy().split("\\.");

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                    if (post.getAssignedTo().equalsIgnoreCase(Objects.requireNonNull(dataSnapshot.child("assignedTo").getValue()).toString()) && post.getTimeStamp().equalsIgnoreCase(Objects.requireNonNull(dataSnapshot.child("timeStamp").getValue()).toString())) {
                                        Log.i("TAG", "freelancerAcceptedTheTask: " + dataSnapshot.getKey());

                                        post.setFreelancerAcceptedTask(true);
                                        post.setStatus("In Progress");
                                        post.setUniqueIdentifier(Objects.requireNonNull(dataSnapshot.getKey()).substring(1));

                                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(Objects.requireNonNull(dataSnapshot.getKey())).child("freelancerAcceptedTask").setValue(post.isFreelancerAcceptedTask());
                                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(Objects.requireNonNull(dataSnapshot.getKey())).child("status").setValue(post.getStatus());
                                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(Objects.requireNonNull(dataSnapshot.getKey())).child("uniqueIdentifier").setValue(post.getUniqueIdentifier());

                                        Log.i("TAG", "freelancerAcceptedTheTask: 1 " + post.getUniqueIdentifier());
                                        Log.i("TAG", "freelancerAcceptedTheTask: 2 " + dataSnapshot.child("uniqueIdentifier").getValue());
                                    }
                                }

                                isButtonClicked = true;

                                Toast.makeText(FreelancerSelectedPostActivity.this, "Task Accepted!", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(FreelancerSelectedPostActivity.this, FreelancerTaskActivity.class));
                                finish();
                            } else {
                                showSomethingWentWrongError();

                                isButtonClicked = false;
                            }
                        });
                    }, 5000);

                    Toast.makeText(this, "Payment is completed", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.alert_no, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
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

    @SuppressLint("SetTextI18n")
    private void addDataToFields() {
        title.setText(post.getTitle());
        description.setText(post.getDescription());
        address.setText(post.getAddress());
        countrySpinner.setText(post.getCountry());
        stateSpinner.setText(post.getState());
        citySpinner.setText(post.getCity());
        zipCode.setText(post.getZipCode());
        startDate.setText(post.getStartDate());
        startTime.setText(post.getStartTime());
        endDate.setText(post.getEndDate());
        endTime.setText(post.getEndTime());
        link.setText(post.getLink());
        assignedBy.setText(post.getAssignedBy());
        amount.setText(post.getAmount());
    }

    private void initializeAllVariables() {
        countrySpinner = findViewById(R.id.freelancer_selected_post_countries_field);
        stateSpinner = findViewById(R.id.freelancer_selected_post_states_field);
        citySpinner = findViewById(R.id.freelancer_selected_post_cities_field);
        title = findViewById(R.id.freelancer_selected_post_title_field);
        description = findViewById(R.id.freelancer_selected_post_description_field);
        address = findViewById(R.id.freelancer_selected_post_address_field);
        zipCode = findViewById(R.id.freelancer_selected_post_zip_code_field);
        startDate = findViewById(R.id.freelancer_selected_post_start_date_field);
        startTime = findViewById(R.id.freelancer_selected_post_start_time_field);
        endDate = findViewById(R.id.freelancer_selected_post_end_date_field);
        endTime = findViewById(R.id.freelancer_selected_post_end_time_field);
        link = findViewById(R.id.freelancer_selected_post_link_field);
        acceptTaskButton = findViewById(R.id.freelancer_selected_post_accept_task_button);
        addProofButton = findViewById(R.id.freelancer_selected_post_add_proof_button);
        backImageView = findViewById(R.id.freelancer_selected_post_back_arrow_image);
        assignedBy = findViewById(R.id.freelancer_selected_post_assigned_by_field);
        amount = findViewById(R.id.freelancer_selected_post_amount_field);
    }
}