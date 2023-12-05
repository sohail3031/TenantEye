package com.example.tenanteye.freelancerhomepages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.tenanteye.Post;
import com.example.tenanteye.R;
import com.google.android.material.card.MaterialCardView;

public class FreelancerAddEvidenceOptionActivity extends AppCompatActivity {
    private ImageView backImageView;
    private MaterialCardView cameraOption, uploadOption;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer_add_evidence_option);

        initializeAllVariables();
        getDataFromPreviousActivity();

        backImageView.setOnClickListener(view -> showAlertMessage());

        cameraOption.setOnClickListener(view -> {
            Intent intent = new Intent(this, FreelancerCameraEvidenceActivity.class);

            intent.putExtra("post", post);

            startActivity(intent);
            finish();
        });

        uploadOption.setOnClickListener(view -> {
            Intent intent = new Intent(this, FreelancerUploadEvidenceActivity.class);

            intent.putExtra("post", post);

            startActivity(intent);
            finish();
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

    private void getDataFromPreviousActivity() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        post = (Post) bundle.getSerializable("post");
    }

    private void initializeAllVariables() {
        backImageView = findViewById(R.id.freelancer_add_evidence_back_arrow_image);
        cameraOption = findViewById(R.id.freelancer_add_evidence_camera_option);
        uploadOption = findViewById(R.id.freelancer_add_evidence_upload_option);
    }
}