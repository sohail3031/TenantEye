package com.example.tenanteye.resetpassword;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.tenanteye.R;
import com.example.tenanteye.login.LoginActivity;

public class ResetPasswordMobileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_mobile);

        ImageView backImage = findViewById(R.id.reset_password_mobile_back_image_view);
        AppCompatButton setNewPassword = findViewById(R.id.reset_password_mobile_password_set_password_button_view);

        backImage.setOnClickListener(view -> {
            finish();
        });

        setNewPassword.setOnClickListener(view -> {
            startActivity(new Intent(this, ResetPasswordSuccessActivity.class));
            finish();
        });
    }
}