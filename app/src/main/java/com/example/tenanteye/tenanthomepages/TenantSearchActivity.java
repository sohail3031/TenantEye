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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tenanteye.FreelancerAdapter;
import com.example.tenanteye.R;
import com.example.tenanteye.User;
import com.example.tenanteye.databinding.ActivityTenantSearchBinding;
import com.example.tenanteye.login.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class TenantSearchActivity extends AppCompatActivity {
    ActivityTenantSearchBinding binding;
    private EditText searchEditText;
    private TextView noDataTextView;
    private ListView listView;
    private DatabaseReference databaseReference;
    private ArrayList<User> freelancersArrayList = new ArrayList<>();
    private ArrayList<String> freelancersName = new ArrayList<>();
    private ArrayList<String> freelancersLocation = new ArrayList<>();
    private FreelancerAdapter freelancerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences loginSharedPreference = getSharedPreferences("login", Context.MODE_PRIVATE);
        String emailAddress = loginSharedPreference.getString("emailAddress", "");

        if (emailAddress.equals("")) {
            Toast.makeText(this, "Please login in again!", Toast.LENGTH_LONG).show();

            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        binding = ActivityTenantSearchBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.tenantSearchBottomNavigationView.setSelectedItemId(R.id.tenant_bottom_menu_search);

        binding.tenantSearchBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.tenant_bottom_menu_task) {
                startActivity(new Intent(this, TenantTaskActivity.class));
            } else if (itemId == R.id.tenant_bottom_menu_create) {
                startActivity(new Intent(this, TenantCreateActivity.class));
            } else if (itemId == R.id.tenant_bottom_menu_more) {
                startActivity(new Intent(this, TenantMoreActivity.class));
            } else if (itemId == R.id.tenant_bottom_menu_chat) {
                startActivity(new Intent(this, TenantChatActivity.class));
            }

            return true;
        });

        initializeAllVariables();
        getFreelancersDataFromDatabase();

        new Handler().postDelayed(() -> {
            if (freelancersArrayList.size() == 0) {
                noDataTextView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
                noDataTextView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }

            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                }
            });
        }, 1000);
    }

    private void getFreelancersDataFromDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (Objects.requireNonNull(dataSnapshot1.child("user").getValue()).toString().equalsIgnoreCase("freelancer")) {
                            User freelancerData = new User();

                            freelancerData.setCity(Objects.requireNonNull(dataSnapshot1.child("city").getValue()).toString());
                            freelancerData.setCountry(Objects.requireNonNull(dataSnapshot1.child("country").getValue()).toString());
                            freelancerData.setEmailAddress(Objects.requireNonNull(dataSnapshot1.child("emailAddress").getValue()).toString());
                            freelancerData.setFirstName(Objects.requireNonNull(dataSnapshot1.child("firstName").getValue()).toString());
                            freelancerData.setGender(Objects.requireNonNull(dataSnapshot1.child("gender").getValue()).toString());
                            freelancerData.setLastName(Objects.requireNonNull(dataSnapshot1.child("lastName").getValue()).toString());
                            freelancerData.setState(Objects.requireNonNull(dataSnapshot1.child("state").getValue()).toString());
                            freelancerData.setProfilePicture(Objects.requireNonNull(dataSnapshot1.child("profilePicture").getValue()).toString());

                            freelancersName.add(Objects.requireNonNull(dataSnapshot1.child("firstName").getValue()) + " " + Objects.requireNonNull(dataSnapshot1.child("lastName").getValue()));
                            freelancersLocation.add(Objects.requireNonNull(dataSnapshot1.child("city").getValue()) + ", " + Objects.requireNonNull(dataSnapshot1.child("state").getValue()) + ", " + Objects.requireNonNull(dataSnapshot1.child("country").getValue()));
                            freelancersArrayList.add(freelancerData);
                        }
                    }
                }

                freelancerAdapter = new FreelancerAdapter(TenantSearchActivity.this, R.layout.freelancer_info_card_view, freelancersArrayList);

                listView.setAdapter(freelancerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeAllVariables() {
        searchEditText = findViewById(R.id.tenant_search_field);
        noDataTextView = findViewById(R.id.tenant_search_no_data_text);
        listView = findViewById(R.id.tenant_search_list_view);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Data");
    }
}