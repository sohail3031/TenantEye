package com.example.tenanteye.freelancerhomepages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.tenanteye.R;
import com.example.tenanteye.databinding.ActivityFreelancerAllTasksBinding;
import com.example.tenanteye.databinding.ActivityFreelancerTaskBinding;

public class FreelancerAllTasksActivity extends AppCompatActivity {
    ActivityFreelancerAllTasksBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFreelancerAllTasksBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        binding.freelancerAllTaskBottomNavigationView.setBackground(null);
        binding.freelancerAllTaskBottomNavigationView.setSelectedItemId(R.id.freelancer_bottom_menu_all_tasks);


        binding.freelancerAllTaskBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.freelancer_bottom_menu_task) {
                startActivity(new Intent(FreelancerAllTasksActivity.this, FreelancerTaskActivity.class));
            } else if (itemId == R.id.freelancer_bottom_menu_more) {
                startActivity(new Intent(FreelancerAllTasksActivity.this, FreelancerMoreActivity.class));
            } else if (itemId == R.id.freelancer_bottom_menu_chat) {
                startActivity(new Intent(FreelancerAllTasksActivity.this, FreelancerChatActivity.class));
            }

            return true;
        });
    }
}