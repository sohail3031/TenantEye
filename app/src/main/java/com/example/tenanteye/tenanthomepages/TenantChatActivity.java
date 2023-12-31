package com.example.tenanteye.tenanthomepages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tenanteye.ChatActivity;
import com.example.tenanteye.Post;
import com.example.tenanteye.R;
import com.example.tenanteye.User;
import com.example.tenanteye.UserChatAdapter;
import com.example.tenanteye.databinding.ActivityTenantChatBinding;
import com.example.tenanteye.freelancerhomepages.FreelancerChatActivity;
import com.example.tenanteye.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class TenantChatActivity extends AppCompatActivity {
    private final ArrayList<User> userArrayList = new ArrayList<>();
    private final ArrayList<String> firstNameArrayList = new ArrayList<>();
    private final ArrayList<String> lastNameArrayList = new ArrayList<>();
    private final ArrayList<String> emailArrayList = new ArrayList<>();
    ActivityTenantChatBinding binding;
    private String emailAddress, profilePictureLink;
    private EditText searchEditText;
    private ImageView searchImageView, profielPictureImageView;
    private ListView recyclerView;
    private StorageTask storageTask;
    private Post post;
    private UserChatAdapter userChatAdapter;

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

        binding = ActivityTenantChatBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        binding.tenantChatBottomNavigationView.setBackground(null);
        binding.tenantChatBottomNavigationView.setSelectedItemId(R.id.tenant_bottom_menu_chat);

        binding.tenantChatBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.tenant_bottom_menu_search) {
                startActivity(new Intent(this, TenantSearchActivity.class));
            } else if (itemId == R.id.tenant_bottom_menu_create) {
                startActivity(new Intent(this, TenantCreateActivity.class));
            } else if (itemId == R.id.tenant_bottom_menu_more) {
                startActivity(new Intent(this, TenantMoreActivity.class));
            } else if (itemId == R.id.tenant_bottom_menu_task) {
                startActivity(new Intent(this, TenantTaskActivity.class));
            }

            return true;
        });

        initializeAllVariables();
        getCurrentUserData();
        loadAllUsers();

        new Handler().postDelayed(() -> {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    checkSearchData(charSequence.toString().toLowerCase());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            recyclerView.setOnItemClickListener((adapterView, view, i, l) -> {
                startActivity(new Intent(this, ChatActivity.class));
                finish();
            });
        }, 500);
    }

    private void checkSearchData(String s) {
        if (userArrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);

            ArrayList<User> availableUsers = new ArrayList<>();

            for (int i = 0; i < userArrayList.size(); i++) {
                if (firstNameArrayList.get(i).toLowerCase().contains(s) || lastNameArrayList.get(i).toLowerCase().contains(s) || emailArrayList.get(i).toLowerCase().contains(s)) {
                    availableUsers.add(userArrayList.get(i));
                }
            }

            UserChatAdapter userChatAdapter1 = new UserChatAdapter(TenantChatActivity.this, R.layout.chat_layout, availableUsers);

            recyclerView.setAdapter(userChatAdapter1);
        }
    }

    private void loadAllUsers() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Data");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (!Objects.requireNonNull(dataSnapshot1.child("emailAddress").getValue()).toString().equalsIgnoreCase(emailAddress)) {
                            User user = new User();

                            user.setFirstName(Objects.requireNonNull(dataSnapshot1.child("firstName").getValue()).toString());
                            user.setLastName(Objects.requireNonNull(dataSnapshot1.child("lastName").getValue()).toString());
                            user.setEmailAddress(Objects.requireNonNull(dataSnapshot1.child("emailAddress").getValue()).toString());
                            user.setProfilePicture(Objects.requireNonNull(dataSnapshot1.child("profilePicture").getValue()).toString());

                            userArrayList.add(user);
                            firstNameArrayList.add(Objects.requireNonNull(dataSnapshot1.child("firstName").getValue()).toString());
                            lastNameArrayList.add(Objects.requireNonNull(dataSnapshot1.child("lastName").getValue()).toString());
                            emailArrayList.add(Objects.requireNonNull(dataSnapshot1.child("emailAddress").getValue()).toString());
                        }
                    }
                }

                Log.i("TAG", "onDataChange: " + userArrayList.size());

                userChatAdapter = new UserChatAdapter(TenantChatActivity.this, R.layout.chat_layout, userArrayList);
                recyclerView.setAdapter(userChatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCurrentUserData() {
        String[] splitEmailAddress = emailAddress.split("\\.");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Data");

        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    if (emailAddress.equalsIgnoreCase(Objects.requireNonNull(dataSnapshot.child("emailAddress").getValue()).toString())) {
                        profilePictureLink = Objects.requireNonNull(dataSnapshot.child("profilePicture").getValue()).toString();

                        Picasso.get().load(profilePictureLink).fit().centerCrop().into(profielPictureImageView);

                        break;
                    }
                }
            }
        });
    }

    private void initializeAllVariables() {
        searchImageView = findViewById(R.id.tenant_chat_search);
        profielPictureImageView = findViewById(R.id.tenant_chat_profile_picture);
        recyclerView = findViewById(R.id.tenant_chat_recycler_view);
        searchEditText = findViewById(R.id.tenant_chat_search_field);
    }
}