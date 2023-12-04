package com.example.tenanteye.freelancerhomepages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.tenanteye.R;
import com.example.tenanteye.databinding.ActivityFreelancerHomeBinding;
import com.example.tenanteye.tenanthomepages.TenantChatActivity;
import com.example.tenanteye.tenanthomepages.TenantCreateActivity;
import com.example.tenanteye.tenanthomepages.TenantMoreActivity;
import com.example.tenanteye.tenanthomepages.TenantSearchActivity;
import com.example.tenanteye.tenanthomepages.TenantTaskActivity;
import com.google.android.material.navigation.NavigationBarView;

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

        binding.freelancerHomeBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.freelancer_bottom_menu_all_tasks) {
                startActivity(new Intent(FreelancerHomeActivity.this, FreelancerAllTasksActivity.class));
            } else if (itemId == R.id.freelancer_bottom_menu_task) {
                startActivity(new Intent(FreelancerHomeActivity.this, FreelancerTaskActivity.class));
            } else if (itemId == R.id.freelancer_bottom_menu_chat) {
                startActivity(new Intent(FreelancerHomeActivity.this, FreelancerChatActivity.class));
            } else {
                startActivity(new Intent(FreelancerHomeActivity.this, FreelancerMoreActivity.class));
            }

            return true;
        });
    }
}