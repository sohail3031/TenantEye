package com.example.tenanteye.tenanthomepages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.tenanteye.R;
import com.example.tenanteye.databinding.ActivityTenantHomeBinding;

public class TenantHomeActivity extends AppCompatActivity {
    ActivityTenantHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTenantHomeBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        binding.tenantHomeBottomNavigationView.setBackground(null);
        binding.tenantHomeBottomNavigationView.setSelectedItemId(R.id.tenant_bottom_menu_task);


        if (savedInstanceState == null) {
            startActivity(new Intent(this, TenantTaskActivity.class));
            finish();
        }


        binding.tenantHomeBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.tenant_bottom_menu_search) {
                startActivity(new Intent(this, TenantSearchActivity.class));
            } else if (itemId == R.id.tenant_bottom_menu_task) {
                startActivity(new Intent(this, TenantTaskActivity.class));
            } else if (itemId == R.id.tenant_bottom_menu_create) {
                startActivity(new Intent(this, TenantCreateActivity.class));
            } else if (itemId == R.id.tenant_bottom_menu_chat) {
                startActivity(new Intent(this, TenantChatActivity.class));
            } else {
                startActivity(new Intent(this, TenantMoreActivity.class));
            }

            return true;
        });
    }
}