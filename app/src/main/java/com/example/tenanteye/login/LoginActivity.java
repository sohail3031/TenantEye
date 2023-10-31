package com.example.tenanteye.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.tenanteye.R;
import com.example.tenanteye.forgotpassword.ForgotPasswordActivity;
import com.example.tenanteye.homepages.HomeActivity;
import com.example.tenanteye.signup.SignUpOneActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AppCompatButton signInButton = findViewById(R.id.login_sign_in_button);
        AppCompatButton forgotPasswordButton = findViewById(R.id.login_forgot_password_button);
        AppCompatButton signUpButton = findViewById(R.id.login_new_user_signup_button);

        signInButton.setOnClickListener(view -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        forgotPasswordButton.setOnClickListener(view -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });

        signUpButton.setOnClickListener(view -> {
            startActivity(new Intent(this, SignUpOneActivity.class));
        });
    }
}