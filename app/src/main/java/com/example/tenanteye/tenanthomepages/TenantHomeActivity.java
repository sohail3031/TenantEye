package com.example.tenanteye.tenanthomepages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

        if (savedInstanceState == null) {
            replaceFragment(new TenantTaskFragment());
        }

        binding.tenantHomeBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.tenant_bottom_menu_search) {
                replaceFragment(new TenantSearchFragment());
            } else if (itemId == R.id.tenant_bottom_menu_task) {
                replaceFragment(new TenantTaskFragment());
            } else if (itemId == R.id.tenant_bottom_menu_create) {
                replaceFragment(new TenantCreateFragment());
            } else if (itemId == R.id.tenant_bottom_menu_chat) {
                replaceFragment(new TenantCreateFragment());
            } else {
                replaceFragment(new TenantMoreFragment());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.tenant_home_frame_layout, fragment);
        fragmentTransaction.commit();
    }
}