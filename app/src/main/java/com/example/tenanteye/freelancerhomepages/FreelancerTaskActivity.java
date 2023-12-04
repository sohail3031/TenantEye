package com.example.tenanteye.freelancerhomepages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tenanteye.Post;
import com.example.tenanteye.TenantPostAdapter;
import com.example.tenanteye.R;
import com.example.tenanteye.databinding.ActivityFreelancerTaskBinding;
import com.example.tenanteye.login.LoginActivity;
import com.example.tenanteye.tenanthomepages.TenantSelectedPostActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class FreelancerTaskActivity extends AppCompatActivity {
    private final ArrayList<Post> postArrayList = new ArrayList<>();
    private final ArrayList<String> titleArrayList = new ArrayList<>();
    private final ArrayList<String> descriptionArrayList = new ArrayList<>();
    private final ArrayList<String> addressArrayList = new ArrayList<>();
    private final ArrayList<String> statusArrayList = new ArrayList<>();
    private final ArrayList<String> assignedByArrayList = new ArrayList<>();
    ActivityFreelancerTaskBinding binding;
    private EditText searchEditText;
    private TextView noDataTextView;
    private ListView listView;
    private String emailAddress, timeStamp;
    private DatabaseReference databaseReference;
    private FreelancerPostAdapter freelancerPostAdapter;

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

        binding = ActivityFreelancerTaskBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        binding.freelancerTaskBottomNavigationView.setBackground(null);
        binding.freelancerTaskBottomNavigationView.setSelectedItemId(R.id.freelancer_bottom_menu_task);


        binding.freelancerTaskBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.freelancer_bottom_menu_all_tasks) {
                startActivity(new Intent(FreelancerTaskActivity.this, FreelancerAllTasksActivity.class));
            } else if (itemId == R.id.freelancer_bottom_menu_more) {
                startActivity(new Intent(FreelancerTaskActivity.this, FreelancerMoreActivity.class));
            } else if (itemId == R.id.freelancer_bottom_menu_chat) {
                startActivity(new Intent(FreelancerTaskActivity.this, FreelancerChatActivity.class));
            }

            return true;
        });

        initializeAllVariables();
        getPostDataFromDatabase();

        new Handler().postDelayed(() -> {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    checkSearchInData(charSequence.toString().toLowerCase());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            listView.setOnItemClickListener((adapterView, view, i, l) -> {
                Intent intent = new Intent(this, FreelancerSelectedPostActivity.class);

                intent.putExtra("post", postArrayList.get(i));

                startActivity(intent);
                finish();
            });
        }, 1000);

        new Handler().postDelayed(() -> {
            if (postArrayList.size() == 0) {
                noDataTextView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
                noDataTextView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }

            Log.i("TAG", "onCreate: " + postArrayList.size());
        }, 1000);
    }

    private void checkSearchInData(String s) {
        if (postArrayList.size() > 0) {
            noDataTextView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            ArrayList<Post> posts = new ArrayList<>();

            for (int i = 0; i < postArrayList.size(); i++) {
                if (titleArrayList.get(i).toLowerCase().contains(s) || descriptionArrayList.get(i).toLowerCase().contains(s) || addressArrayList.get(i).toLowerCase().contains(s) || statusArrayList.get(i).toLowerCase().contains(s) || assignedByArrayList.get(i).toLowerCase().contains(s)) {
                    posts.add(postArrayList.get(i));
                }
            }

            FreelancerPostAdapter freelancerPostAdapter1 = new FreelancerPostAdapter(this, R.layout.freelancer_show_info_breif, posts);

            listView.setAdapter(freelancerPostAdapter1);
        } else {
            noDataTextView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    private void getPostDataFromDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (Objects.requireNonNull(dataSnapshot1.child("assignedTo").getValue()).toString().equalsIgnoreCase(emailAddress)) {
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
                            post.setAssignedTo(Objects.requireNonNull(dataSnapshot1.child("assignedTo").getValue()).toString());
                            post.setAssignedBy(Objects.requireNonNull(dataSnapshot1.child("assignedBy").getValue()).toString());
                            post.setFreelancerAcceptedTask((boolean) Objects.requireNonNull(dataSnapshot1.child("freelancerAcceptedTask").getValue()));

                            titleArrayList.add(Objects.requireNonNull(dataSnapshot1.child("title").getValue()).toString());
                            descriptionArrayList.add(Objects.requireNonNull(dataSnapshot1.child("description").getValue()).toString());
                            addressArrayList.add(Objects.requireNonNull(dataSnapshot1.child("address").getValue()).toString());
                            statusArrayList.add(Objects.requireNonNull(dataSnapshot1.child("status").getValue()).toString());
                            assignedByArrayList.add(Objects.requireNonNull(dataSnapshot1.child("assignedBy").getValue()).toString());
                            postArrayList.add(post);
                        }
                    }
                }

                freelancerPostAdapter = new FreelancerPostAdapter(FreelancerTaskActivity.this, R.layout.freelancer_show_info_breif, postArrayList);

                listView.setAdapter(freelancerPostAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        searchEditText = findViewById(R.id.freelancer_task_search);
        listView = findViewById(R.id.freelancer_task_list_view);
        noDataTextView = findViewById(R.id.freelancer_post_no_data_text);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
    }
}