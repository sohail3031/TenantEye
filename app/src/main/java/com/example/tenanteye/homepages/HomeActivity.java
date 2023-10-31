package com.example.tenanteye.homepages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.tenanteye.R;
import com.example.tenanteye.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        replaceFragment(new SearchFragment());
        binding.homeBottomNavigationView.setBackground(null);

        binding.homeBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_menu_search) {
                replaceFragment(new SearchFragment());
            } else if (itemId == R.id.bottom_menu_task) {
                replaceFragment(new TaskFragment());
            } else if (itemId == R.id.bottom_menu_create) {
                replaceFragment(new CreateFragment());
            } else if (itemId == R.id.bottom_menu_chat) {
                replaceFragment(new ChatFragment());
            } else {
                replaceFragment(new MoreFragment());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.home_frame_layout, fragment);
        fragmentTransaction.commit();
    }
}