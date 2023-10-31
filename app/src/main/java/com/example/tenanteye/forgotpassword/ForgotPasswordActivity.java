package com.example.tenanteye.forgotpassword;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.tenanteye.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ImageView backImageView = findViewById(R.id.forgot_password_back_arrow_image);
        AppCompatButton nextButton = findViewById(R.id.forgot_password_next_button);
        AppCompatButton loginButton = findViewById(R.id.forgot_password_login_button);

        backImageView.setOnClickListener(view -> {
            finish();
        });

        nextButton.setOnClickListener(view -> {
            startActivity(new Intent(this, ForgotPasswordSelectionActivity.class));
            finish();
        });

        loginButton.setOnClickListener(view -> {
            finish();
        });
    }
}