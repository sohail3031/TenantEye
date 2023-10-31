package com.example.tenanteye.forgotpassword;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.tenanteye.R;
import com.example.tenanteye.resetpassword.ResetPasswordMobileActivity;

public class ForgotPasswordMobileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_mobile);

        ImageView backImage = findViewById(R.id.forgot_password_mobile_back_image_button);
        AppCompatButton verifyCode = findViewById(R.id.forgot_password_mobile_button);

        backImage.setOnClickListener(view -> {
            finish();
        });

        verifyCode.setOnClickListener(view -> {
            startActivity(new Intent(this, ResetPasswordMobileActivity.class));
            finish();
        });
    }
}