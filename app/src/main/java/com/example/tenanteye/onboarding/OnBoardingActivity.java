package com.example.tenanteye.onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.tenanteye.R;

public class OnBoardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ViewPager viewPager = findViewById(R.id.on_boarding_view_pager);
        Button skipButton = findViewById(R.id.on_boarding_skip_button);
        Button letsGetStartedButton = findViewById(R.id.on_boarding_lets_get_started);
        LinearLayout dotsLinearLayout = findViewById(R.id.on_boarding_linear_layout_dots);
        Button navigateButton = findViewById(R.id.on_boarding_navigation_button);

    }
}