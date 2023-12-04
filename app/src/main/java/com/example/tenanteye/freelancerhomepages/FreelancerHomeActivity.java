package com.example.tenanteye.freelancerhomepages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.tenanteye.R;
import com.example.tenanteye.databinding.ActivityFreelancerHomeBinding;

public class FreelancerHomeActivity extends AppCompatActivity {
    ActivityFreelancerHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFreelancerHomeBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        binding.freelancerHomeBottomNavigationView.setBackground(null);
        binding.freelancerHomeBottomNavigationView.setSelectedItemId(R.id.freelancer_bottom_menu_task);

        if (savedInstanceState == null) {
            startActivity(new Intent(this, FreelancerTaskActivity.class));
            finish();
        }
    }
}