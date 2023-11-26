package com.example.tenanteye.forgotpassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tenanteye.R;
import com.example.tenanteye.login.LoginActivity;
import com.example.tenanteye.resetpassword.ResetPasswordEmailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class ForgotPasswordSelectionActivity extends AppCompatActivity {
    private ImageView backImage;
    private Button mobileButton, emailButton;
    private TextView mobileTextView, emailTextView;
    private Bundle bundle;
    private String emailAddress, phoneNumber;
    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_selection);

        initializeAllVariables();
        getUserDataFromBundle();
        showUserDataOnUI();

        backImage.setOnClickListener(view -> {
            showAlertMessage();
        });

        mobileButton.setOnClickListener(view -> {
            startActivity(new Intent(this, ForgotPasswordMobileActivity.class));
            finish();
        });

        emailButton.setOnClickListener(view -> {
            sendResetPasswordEmail();
        });
    }

    private void sendResetPasswordEmail() {
        firebaseAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(ForgotPasswordSelectionActivity.this, ResetPasswordEmailActivity.class));
                } else {
                    Toast.makeText(ForgotPasswordSelectionActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showUserDataOnUI() {
        mobileTextView.setText(phoneNumber);
        emailTextView.setText(emailAddress);
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

    private void getUserDataFromBundle() {
        emailAddress = bundle.getString("emailAddress");
        phoneNumber = bundle.getString("phoneNumber");
    }

    private void initializeAllVariables() {
        backImage = findViewById(R.id.forgot_password_selection_back_image_view);
        mobileButton = findViewById(R.id.forgot_password_selection_mobile_button);
        emailButton = findViewById(R.id.forgot_password_selection_email_button);
        mobileTextView = findViewById(R.id.forgot_password_selection_mobile_text_view_info);
        emailTextView = findViewById(R.id.forgot_password_selection_email_text_view_info);

        bundle = getIntent().getExtras();
        firebaseAuth = FirebaseAuth.getInstance();
    }
}