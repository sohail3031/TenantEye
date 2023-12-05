package com.example.tenanteye.freelancerhomepages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tenanteye.FreelancerAdapter;
import com.example.tenanteye.Post;
import com.example.tenanteye.R;
import com.example.tenanteye.User;
import com.example.tenanteye.databinding.ActivityFreelancerAllTasksBinding;
import com.example.tenanteye.databinding.ActivityFreelancerTaskBinding;
import com.example.tenanteye.login.LoginActivity;
import com.example.tenanteye.tenanthomepages.TenantSearchActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class FreelancerAllTasksActivity extends AppCompatActivity {
    private final ArrayList<Post> postArrayList = new ArrayList<>();
    private final ArrayList<String> titleArrayList = new ArrayList<>();
    private final ArrayList<String> descriptionArrayList = new ArrayList<>();
    private final ArrayList<String> addressArrayList = new ArrayList<>();
    private final ArrayList<String> cityArrayList = new ArrayList<>();
    private final ArrayList<String> stateArrayList = new ArrayList<>();
    ActivityFreelancerAllTasksBinding binding;
    private EditText searchEditText;
    private TextView noTasksAvailableTextView;
    private ListView listView;
    private String emailAddress;
    private DatabaseReference databaseReference;
    private FreelancerAvailablePosts freelancerAvailablePosts;

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

        binding = ActivityFreelancerAllTasksBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        binding.freelancerAllTasksBottomNavigationView.setBackground(null);
        binding.freelancerAllTasksBottomNavigationView.setSelectedItemId(R.id.freelancer_bottom_menu_all_tasks);


        binding.freelancerAllTasksBottomNavigationView.setOnItemSelectedListener(item -> {
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

        initializeAllVariables();
        getTasksFromDatabase();

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

            listView.setOnItemClickListener((adapterView, view, i, l) -> {
                Intent intent = new Intent(this, FreelancerShowSelectedPostActivity.class);

                intent.putExtra("post", postArrayList.get(i));

                startActivity(intent);
                finish();
            });
        }, 500);
    }

    private void checkSearchData(String s) {
        if (postArrayList.size() > 0) {
            noTasksAvailableTextView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            ArrayList<Post> availablePosts = new ArrayList<>();

            for (int i = 0; i < postArrayList.size(); i++) {
                if (titleArrayList.get(i).toLowerCase().contains(s) || descriptionArrayList.get(i).toLowerCase().contains(s) || addressArrayList.get(i).toLowerCase().contains(s) || cityArrayList.get(i).toLowerCase().contains(s) || stateArrayList.get(i).toLowerCase().contains(s)) {
                    availablePosts.add(postArrayList.get(i));
                }
            }

            FreelancerAvailablePosts freelancerAvailablePosts1 = new FreelancerAvailablePosts(FreelancerAllTasksActivity.this, R.layout.freelancer_availabe_post, availablePosts);

            listView.setAdapter(freelancerAvailablePosts1);
        } else {
            noTasksAvailableTextView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    private void getTasksFromDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (Objects.requireNonNull(dataSnapshot1.child("status").getValue()).toString().equalsIgnoreCase("active") && Objects.requireNonNull(dataSnapshot1.child("assignedTo").getValue()).toString().equalsIgnoreCase("") && !Objects.requireNonNull(dataSnapshot1.child("assignedBy").getValue()).toString().equalsIgnoreCase(emailAddress)) {
                            Post post = new Post();

                            post.setAddress(Objects.requireNonNull(dataSnapshot1.child("address").getValue()).toString());
                            post.setCity(Objects.requireNonNull(dataSnapshot1.child("city").getValue()).toString());
                            post.setCountry(Objects.requireNonNull(dataSnapshot1.child("country").getValue()).toString());
                            post.setDescription(Objects.requireNonNull(dataSnapshot1.child("description").getValue()).toString());
                            post.setEndDate(Objects.requireNonNull(dataSnapshot1.child("endDate").getValue()).toString());
                            post.setEndTime(Objects.requireNonNull(dataSnapshot1.child("endTime").getValue()).toString());
                            post.setLink(Objects.requireNonNull(dataSnapshot1.child("link").getValue()).toString());
                            post.setStartDate(Objects.requireNonNull(dataSnapshot1.child("startDate").getValue()).toString());
                            post.setStartTime(Objects.requireNonNull(dataSnapshot1.child("startTime").getValue()).toString());
                            post.setState(Objects.requireNonNull(dataSnapshot1.child("state").getValue()).toString());
                            post.setStatus(Objects.requireNonNull(dataSnapshot1.child("status").getValue()).toString());
                            post.setTimeStamp(Objects.requireNonNull(dataSnapshot1.child("timeStamp").getValue()).toString());
                            post.setTitle(Objects.requireNonNull(dataSnapshot1.child("title").getValue()).toString());
                            post.setZipCode(Objects.requireNonNull(dataSnapshot1.child("zipCode").getValue()).toString());
                            post.setAssignedBy(Objects.requireNonNull(dataSnapshot1.child("assignedBy").getValue()).toString());

                            postArrayList.add(post);
                            titleArrayList.add(post.getTitle());
                            descriptionArrayList.add(post.getDescription());
                            addressArrayList.add(post.getAddress());
                            cityArrayList.add(post.getCity());
                            stateArrayList.add(post.getState());
                        }
                    }
                }

                freelancerAvailablePosts = new FreelancerAvailablePosts(FreelancerAllTasksActivity.this, R.layout.freelancer_availabe_post, postArrayList);

                listView.setAdapter(freelancerAvailablePosts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeAllVariables() {
        searchEditText = findViewById(R.id.freelancer_all_tasks_search);
        noTasksAvailableTextView = findViewById(R.id.freelancer_all_tasks_no_data_text);
        listView = findViewById(R.id.freelancer_all_tasks_list_view);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
    }
}