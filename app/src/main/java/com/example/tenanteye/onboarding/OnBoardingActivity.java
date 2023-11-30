package com.example.tenanteye.onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tenanteye.R;
import com.example.tenanteye.SliderAdapter;
import com.example.tenanteye.login.LoginActivity;

public class OnBoardingActivity extends AppCompatActivity {
    private LinearLayout dotsLinearLayout;
    private Button letsGetStartedButton;
    private int currentPosition;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ViewPager viewPager = findViewById(R.id.on_boarding_view_pager);
        Button skipButton = findViewById(R.id.on_boarding_skip_button);
        letsGetStartedButton = findViewById(R.id.on_boarding_lets_get_started);
        dotsLinearLayout = findViewById(R.id.on_boarding_linear_layout_dots);
        Button navigateButton = findViewById(R.id.on_boarding_navigation_button);

        SliderAdapter sliderAdapter = new SliderAdapter(this);

        viewPager.setAdapter(sliderAdapter);
        addDots(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addDots(position);

                currentPosition = position;

                if (position == 0) {
                    letsGetStartedButton.setVisibility(View.INVISIBLE);
                } else if (position == 1) {
                    letsGetStartedButton.setVisibility(View.INVISIBLE);
                } else if (position == 2) {
                    letsGetStartedButton.setVisibility(View.INVISIBLE);
                } else if (position == 3) {
                    letsGetStartedButton.setVisibility(View.INVISIBLE);
                } else {
                    animation = AnimationUtils.loadAnimation(OnBoardingActivity.this, R.anim.landing_page_animation);

                    letsGetStartedButton.setAnimation(animation);
                    letsGetStartedButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        skipButton.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        navigateButton.setOnClickListener(view -> {
            if (currentPosition < 5) {
                viewPager.setCurrentItem(currentPosition + 1);
            }
        });

        letsGetStartedButton.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void addDots(int position) {
        TextView[] dots = new TextView[5];

        dotsLinearLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);

            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dotsLinearLayout.addView(dots[i]);
        }

        dots[position].setTextColor(getResources().getColor(R.color.black));
    }
}