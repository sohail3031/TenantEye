package com.example.tenanteye.freelancerhomepages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.tenanteye.R;
import com.example.tenanteye.databinding.ActivityFreelancerAllTasksBinding;
import com.example.tenanteye.databinding.ActivityFreelancerChatBinding;

public class FreelancerChatActivity extends AppCompatActivity {
    ActivityFreelancerChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFreelancerChatBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        binding.freelancerChatBottomNavigationView.setBackground(null);
        binding.freelancerChatBottomNavigationView.setSelectedItemId(R.id.freelancer_bottom_menu_chat);


        binding.freelancerChatBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.freelancer_bottom_menu_task) {
                startActivity(new Intent(FreelancerChatActivity.this, FreelancerTaskActivity.class));
            } else if (itemId == R.id.freelancer_bottom_menu_more) {
                startActivity(new Intent(FreelancerChatActivity.this, FreelancerMoreActivity.class));
            } else if (itemId == R.id.freelancer_bottom_menu_all_tasks) {
                startActivity(new Intent(FreelancerChatActivity.this, FreelancerAllTasksActivity.class));
            }

            return true;
        });
    }
}