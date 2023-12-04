package com.example.tenanteye.freelancerhomepages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tenanteye.R;
import com.example.tenanteye.User;
import com.example.tenanteye.tenanthomepages.TenantMoreActivity;
import com.example.tenanteye.tenanthomepages.TenantUpdateProfilePictureActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class FreelancerUpdateProfilePictureActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView backImageView, uploadImage, imageView;
    private TextView uploadText;
    private AppCompatButton cancelButton, saveButton;
    private ProgressBar progressBar;
    private Uri uri;
    private StorageTask storageTask;
    private String emailAddress, profilePictureLink;
    private User user;
    private boolean isUploading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer_update_profile_picture);

        initializeAllVariables();
        getDataFromPreviousActivity();

        backImageView.setOnClickListener(view -> showAlertMessage());
        imageView.setOnClickListener(view -> openFileChooser());
        uploadImage.setOnClickListener(view -> openFileChooser());
        uploadText.setOnClickListener(view -> openFileChooser());
        cancelButton.setOnClickListener(view -> cancelButtonClicked());
        saveButton.setOnClickListener(view -> {
            if (isUploading && storageTask != null && storageTask.isInProgress()) {
                Toast.makeText(this, "Upload In Progress!", Toast.LENGTH_SHORT).show();
            } else {
                savePictureToDatabase();
            }
        });
    }

    private void getDataFromPreviousActivity() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        user = (User) bundle.getSerializable("user");
        assert user != null;
        emailAddress = user.getEmailAddress();
    }

    private void updateUserData() {
        String[] splitEmailAddress = emailAddress.split("\\.");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Data");

        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    if (Objects.requireNonNull(dataSnapshot.child("emailAddress").getValue()).toString().equalsIgnoreCase(emailAddress)) {
                        String key = Objects.requireNonNull(dataSnapshot.getKey());

                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).child("profilePicture").setValue(user.getProfilePicture());

                        Toast.makeText(FreelancerUpdateProfilePictureActivity.this, "Profile Picture Updated!", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(FreelancerUpdateProfilePictureActivity.this, FreelancerMoreActivity.class));
                        finish();
                    }
                }
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void savePictureToDatabase() {
        String[] splitEmailAddress = emailAddress.split("\\.");

        if (uri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(splitEmailAddress[0] + "-" + splitEmailAddress[1] + "/" + System.currentTimeMillis() + "." + getFileExtension(uri));

            isUploading = true;
            storageTask = storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                        new Handler().postDelayed(() -> progressBar.setProgress(0), 500);
                        Toast.makeText(FreelancerUpdateProfilePictureActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();

//                            profilePictureLink = storageReference.getDownloadUrl().toString();
                        storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                            profilePictureLink = task.getResult().toString();
                            user.setProfilePicture(profilePictureLink);
                        });

                        new Handler().postDelayed(this::updateUserData, 500);
                    })
                    .addOnFailureListener(e -> Toast.makeText(FreelancerUpdateProfilePictureActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnProgressListener(snapshot -> {
                        int progress = (int) (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());

                        progressBar.setProgress(progress);
                    });
        } else {
            Toast.makeText(this, "No File Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();

            Picasso.get().load(uri).into(imageView);
            uploadImage.setVisibility(View.INVISIBLE);
            uploadText.setVisibility(View.INVISIBLE);

//            view.setImageURI(uri);
        }
    }

    private void cancelButtonClicked() {
        imageView.setImageResource(R.color.white);
        uploadText.setVisibility(View.VISIBLE);
        uploadImage.setVisibility(View.VISIBLE);
    }

    private void openFileChooser() {
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void showAlertMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle(R.string.sign_up_alert_title)
                .setMessage(R.string.sign_up_alert_message)
                .setPositiveButton(R.string.alert_yes, (dialog, which) -> {
                    startActivity(new Intent(this, FreelancerMoreActivity.class));
                    finish();
                })
                .setNegativeButton(R.string.alert_no, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

    }

    private void initializeAllVariables() {
        backImageView = findViewById(R.id.freelancer_update_profile_picture_back_image_view);
        imageView = findViewById(R.id.freelancer_update_profile_picture_upload_image_view);
        uploadImage = findViewById(R.id.freelancer_update_profile_picture_image_image_view);
        uploadText = findViewById(R.id.freelancer_update_profile_picture_upload_image_text_view);
        cancelButton = findViewById(R.id.freelancer_update_profile_picture_cancel_button);
        saveButton = findViewById(R.id.freelancer_update_profile_picture_save_button);
        progressBar = findViewById(R.id.freelancer_update_profile_picture_progress_bar);
    }
}