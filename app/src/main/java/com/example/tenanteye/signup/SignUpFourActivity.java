package com.example.tenanteye.signup;

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
import com.example.tenanteye.login.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class SignUpFourActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uri;
    private ImageView backImageView, uploadImageView, view;
    private TextView uploadTextView;
    private AppCompatButton cancelButton, saveButton;
    private ProgressBar progressBar;
    private String emailAddress, profilePictureLink;
    private StorageTask storageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_four);

        initializeAllVariables();
        getDataFromPreviousActivity();

        view.setOnClickListener(view -> openFileChooser());
        uploadImageView.setOnClickListener(view -> openFileChooser());
        uploadTextView.setOnClickListener(view -> openFileChooser());
        cancelButton.setOnClickListener(view -> cancelButtonClicked());
        saveButton.setOnClickListener(view -> {
            if (storageTask != null && storageTask.isInProgress()) {
                Toast.makeText(this, "Upload In Progress!", Toast.LENGTH_SHORT).show();
            } else {
                savePictureToDatabase();
            }
        });
        backImageView.setOnClickListener(view -> showAlertMessage());
    }

    private void cancelButtonClicked() {
        view.setImageResource(R.color.white);
        uploadTextView.setVisibility(View.VISIBLE);
        uploadImageView.setVisibility(View.VISIBLE);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void moveToNextActivity() {
        Bundle bundle = getIntent().getExtras();
        Intent intent = new Intent(this, SignUpThreeActivity.class);
        assert bundle != null;

        intent.putExtra("firstName", bundle.getString("firstName"));
        intent.putExtra("lastName", bundle.getString("lastName"));
        intent.putExtra("emailAddress", bundle.getString("emailAddress"));
        intent.putExtra("country", bundle.getString("country"));
        intent.putExtra("state", bundle.getString("state"));
        intent.putExtra("city", bundle.getString("city"));
        intent.putExtra("gender", bundle.getString("gender"));
        intent.putExtra("user", bundle.getString("user"));
        intent.putExtra("dateOfBirth", bundle.getString("dateOfBirth"));
        intent.putExtra("profilePicture", profilePictureLink);

        startActivity(intent);
        finish();
    }

    private void savePictureToDatabase() {
        String[] splitEmailAddress = emailAddress.split("\\.");

        if (uri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(splitEmailAddress[0] + "-" + splitEmailAddress[1] + "/" + System.currentTimeMillis() + "." + getFileExtension(uri));

            storageTask = storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            new Handler().postDelayed(() -> progressBar.setProgress(0), 500);
                            Toast.makeText(SignUpFourActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();

//                            profilePictureLink = storageReference.getDownloadUrl().toString();
                            storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                                profilePictureLink = task.getResult().toString();
                            });

                            new Handler().postDelayed(() -> {
                                moveToNextActivity();
                            }, 500);
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(SignUpFourActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnProgressListener(snapshot -> {
                        int progress = (int) (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());

                        progressBar.setProgress(progress);
                    });
        } else {
            Toast.makeText(this, "No File Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataFromPreviousActivity() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        emailAddress = bundle.getString("emailAddress");
    }

    private void openFileChooser() {
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();

            Picasso.get().load(uri).into(view);
            uploadImageView.setVisibility(View.INVISIBLE);
            uploadTextView.setVisibility(View.INVISIBLE);

//            view.setImageURI(uri);
        }
    }

    private void showAlertMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle(R.string.sign_up_alert_title)
                .setMessage(R.string.sign_up_alert_message)
                .setPositiveButton(R.string.alert_yes, (dialog, which) -> {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton(R.string.alert_no, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

    }

    private void initializeAllVariables() {
        backImageView = findViewById(R.id.sign_up_four_back_image_view);
        view = findViewById(R.id.sign_up_four_upload_image_view);
        uploadImageView = findViewById(R.id.sign_up_four_upload_image_image_view);
        uploadTextView = findViewById(R.id.sign_up_four_upload_image_text_view);
        cancelButton = findViewById(R.id.sign_up_four_cancel_button);
        saveButton = findViewById(R.id.sign_up_four_save_button);
        progressBar = findViewById(R.id.sign_up_four_progress_bar);
    }
}