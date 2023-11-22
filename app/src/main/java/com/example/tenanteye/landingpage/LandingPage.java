package com.example.tenanteye.landingpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tenanteye.R;
import com.example.tenanteye.login.LoginActivity;
import com.example.tenanteye.onboarding.OnBoardingActivity;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LandingPage extends AppCompatActivity {
    private static final int SPLASH_SCREEN_TIME_OUT = 5000;
    private SharedPreferences onBoardingSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TextView landingPageTitleText = findViewById(R.id.landing_page_title_text);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.landing_page_animation);

        landingPageTitleText.setAnimation(animation);

        new Handler().postDelayed(() -> {
            onBoardingSharedPreferences = getSharedPreferences("onBoarding", MODE_PRIVATE);
            boolean isFirstTime = onBoardingSharedPreferences.getBoolean("isFirstTime", true);

            if (isFirstTime) {
                SharedPreferences.Editor editor = onBoardingSharedPreferences.edit();

                editor.putBoolean("firstTime", false);
                editor.apply();

                Intent intent = new Intent(this, OnBoardingActivity.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(landingPageTitleText, "landing_page_text_tenant_transition");
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this, pairs);

                startActivity(intent, activityOptions.toBundle());
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(landingPageTitleText, "landing_page_text_tenant_transition");
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this, pairs);

                startActivity(intent, activityOptions.toBundle());
            }

            finish();
        }, SPLASH_SCREEN_TIME_OUT);
    }
}