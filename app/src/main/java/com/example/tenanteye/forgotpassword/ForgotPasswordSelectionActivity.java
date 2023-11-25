package com.example.tenanteye.forgotpassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.example.tenanteye.R;
import com.example.tenanteye.resetpassword.ResetPasswordEmailActivity;

public class ForgotPasswordSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_selection);

        ImageView backImage = findViewById(R.id.forgot_password_selection_back_image_view);
        Button mobileButton = findViewById(R.id.forgot_password_selection_mobile_button);
        Button emailButton = findViewById(R.id.forgot_password_selection_email_button);

//        backImage.setOnClickListener(view -> {
//            finish();
//        });

        mobileButton.setOnClickListener(view -> {
            startActivity(new Intent(this, ForgotPasswordMobileActivity.class));
            finish();
        });

        emailButton.setOnClickListener(view -> {
            startActivity(new Intent(this, ResetPasswordEmailActivity.class));
            finish();
        });
    }
}