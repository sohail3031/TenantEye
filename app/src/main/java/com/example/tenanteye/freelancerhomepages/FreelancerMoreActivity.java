package com.example.tenanteye.freelancerhomepages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tenanteye.R;
import com.example.tenanteye.User;
import com.example.tenanteye.databinding.ActivityFreelancerChatBinding;
import com.example.tenanteye.databinding.ActivityFreelancerMoreBinding;
import com.example.tenanteye.login.LoginActivity;
import com.example.tenanteye.tenanthomepages.TenantEditProfileActivity;
import com.example.tenanteye.tenanthomepages.TenantUpdatePasswordActivity;
import com.example.tenanteye.tenanthomepages.TenantUpdateProfilePictureActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class FreelancerMoreActivity extends AppCompatActivity {
    ActivityFreelancerMoreBinding binding;
    private String emailAddress;
    private ImageView profilePictureImageView;
    private TextView userNameTextView, userEmailTextView, userInfoTextView;
    private AppCompatButton editProfileButton, updateProfileButton, logoutButton, updatePassword;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences loginSharedPreference = getSharedPreferences("login", Context.MODE_PRIVATE);
        emailAddress = loginSharedPreference.getString("emailAddress", "");

        if (emailAddress.equals("")) {
            Toast.makeText(this, "Please login in again!", Toast.LENGTH_LONG).show();

            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        binding = ActivityFreelancerMoreBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        binding.freelancerMoreBottomNavigationView.setBackground(null);
        binding.freelancerMoreBottomNavigationView.setSelectedItemId(R.id.freelancer_bottom_menu_more);


        binding.freelancerMoreBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.freelancer_bottom_menu_task) {
                startActivity(new Intent(FreelancerMoreActivity.this, FreelancerTaskActivity.class));
            } else if (itemId == R.id.freelancer_bottom_menu_chat) {
                startActivity(new Intent(FreelancerMoreActivity.this, FreelancerChatActivity.class));
            } else if (itemId == R.id.freelancer_bottom_menu_all_tasks) {
                startActivity(new Intent(FreelancerMoreActivity.this, FreelancerAllTasksActivity.class));
            }

            return true;
        });

        initializeAllVariables();
        getCurrentUserData();

        new Handler().postDelayed(this::displayUserData, 500);

        logoutButton.setOnClickListener(view -> logout());
        updateProfileButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, FreelancerUpdateProfilePictureActivity.class);

            intent.putExtra("user", user);

            startActivity(intent);
            finish();
        });
        editProfileButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, FreelancerEditProfileActivity.class);

            intent.putExtra("user", user);

            startActivity(intent);
            finish();
        });
        updatePassword.setOnClickListener(view -> {
            Intent intent = new Intent(this, FreelancerUpdatePasswordActivity.class);

            intent.putExtra("user", user);

            startActivity(intent);
            finish();
        });
    }

    private void logout() {
        SharedPreferences loginSharedPreference = getSharedPreferences("login", Context.MODE_PRIVATE);

        loginSharedPreference.edit().clear().apply();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @SuppressLint("SetTextI18n")
    private void displayUserData() {
        new Handler().postDelayed(() -> {
            Picasso.get().load(user.getProfilePicture()).fit().centerCrop().into(profilePictureImageView);
        }, 500);

        userNameTextView.setText(user.getFirstName() + " " + user.getLastName());
        userEmailTextView.setText(user.getEmailAddress());
        userInfoTextView.setText(user.getUser());
    }

    private void getCurrentUserData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Data");
        String[] splitEmailAddress = emailAddress.split("\\.");

        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    user = new User();

                    user.setState(Objects.requireNonNull(dataSnapshot.child("state").getValue()).toString());
                    user.setCountry(Objects.requireNonNull(dataSnapshot.child("country").getValue()).toString());
                    user.setDateOfBirth(Objects.requireNonNull(dataSnapshot.child("dateOfBirth").getValue()).toString());
                    user.setEmailAddress(Objects.requireNonNull(dataSnapshot.child("emailAddress").getValue()).toString());
                    user.setFirstName(Objects.requireNonNull(dataSnapshot.child("firstName").getValue()).toString());
                    user.setGender(Objects.requireNonNull(dataSnapshot.child("gender").getValue()).toString());
                    user.setLastName(Objects.requireNonNull(dataSnapshot.child("lastName").getValue()).toString());
                    user.setPhoneNumber(Objects.requireNonNull(dataSnapshot.child("phoneNumber").getValue()).toString());
                    user.setProfilePicture(Objects.requireNonNull(dataSnapshot.child("profilePicture").getValue()).toString());
                    user.setState(Objects.requireNonNull(dataSnapshot.child("state").getValue()).toString());
                    user.setUser(Objects.requireNonNull(dataSnapshot.child("user").getValue()).toString());
                    user.setCity(Objects.requireNonNull(dataSnapshot.child("city").getValue()).toString());
                    user.setPassword(Objects.requireNonNull(dataSnapshot.child("password").getValue()).toString());
                }
            } else {
                showSomethingWentWrongError();
            }
        });
    }

    private void showSomethingWentWrongError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle("Something went wrong")
                .setMessage("Please try again")
                .setPositiveButton(R.string.error_alert_okay, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void initializeAllVariables() {
        profilePictureImageView = findViewById(R.id.freelancer_more_profile_picture);
        userNameTextView = findViewById(R.id.freelancer_more_user_name);
        userEmailTextView = findViewById(R.id.freelancer_more_user_email);
        userInfoTextView = findViewById(R.id.freelancer_more_login_info);
        editProfileButton = findViewById(R.id.freelancer_more_edit_profile);
        updateProfileButton = findViewById(R.id.freelancer_more_update_profile_picture);
        logoutButton = findViewById(R.id.freelancer_more_logout);
        updatePassword = findViewById(R.id.freelancer_more_update_password);
    }
}