package com.example.tenanteye.freelancerhomepages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.tenanteye.R;
import com.example.tenanteye.databinding.ActivityFreelancerTaskBinding;

public class FreelancerTaskActivity extends AppCompatActivity {
    ActivityFreelancerTaskBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFreelancerTaskBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        binding.freelancerTaskBottomNavigationView.setBackground(null);
        binding.freelancerTaskBottomNavigationView.setSelectedItemId(R.id.freelancer_bottom_menu_task);


        binding.freelancerTaskBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.freelancer_bottom_menu_all_tasks) {
                startActivity(new Intent(FreelancerTaskActivity.this, FreelancerAllTasksActivity.class));
            } else if (itemId == R.id.freelancer_bottom_menu_more) {
                startActivity(new Intent(FreelancerTaskActivity.this, FreelancerMoreActivity.class));
            } else if (itemId == R.id.freelancer_bottom_menu_chat) {
                startActivity(new Intent(FreelancerTaskActivity.this, FreelancerChatActivity.class));
            }

            return true;
        });
    }
}