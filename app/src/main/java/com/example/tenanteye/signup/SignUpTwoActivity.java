package com.example.tenanteye.signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.tenanteye.R;
import com.example.tenanteye.login.LoginActivity;

public class SignUpTwoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_two);

        ImageView backImageView = findViewById(R.id.sign_up_two_back_image_view);
        AppCompatButton nextButton = findViewById(R.id.sign_up_two_next_button);
        AppCompatButton loginButton = findViewById(R.id.sign_up_two_login_button);

        backImageView.setOnClickListener(view -> {
            finish();
        });

        nextButton.setOnClickListener(view -> {
            startActivity(new Intent(this, SignUpThreeActivity.class));
            finish();
        });

        loginButton.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}