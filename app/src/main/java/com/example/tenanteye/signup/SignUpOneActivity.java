package com.example.tenanteye.signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.tenanteye.R;

public class SignUpOneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_one);

        ImageView backImageView = findViewById(R.id.sign_up_one_back_image_view);
        AppCompatButton loginButton = findViewById(R.id.sign_up_one_login_button_view);
        AppCompatButton nextButton = findViewById(R.id.sign_up_one_next_button_view);

        backImageView.setOnClickListener(view -> {
            finish();
        });

        loginButton.setOnClickListener(view -> {
            finish();
        });

        nextButton.setOnClickListener(view -> {
            startActivity(new Intent(this, SignUpTwoActivity.class));
            finish();
        });
    }
}