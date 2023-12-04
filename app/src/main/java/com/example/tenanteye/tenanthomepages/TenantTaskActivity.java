package com.example.tenanteye.tenanthomepages;

import androidx.appcompat.app.AlertDialog;
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

import com.example.tenanteye.Post;
import com.example.tenanteye.PostAdapter;
import com.example.tenanteye.R;
import com.example.tenanteye.databinding.ActivityTenantTaskBinding;
import com.example.tenanteye.login.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class TenantTaskActivity extends AppCompatActivity {
    private final ArrayList<Post> postArrayList = new ArrayList<>();
    private final ArrayList<String> titleArrayList = new ArrayList<>();
    private final ArrayList<String> descriptionArrayList = new ArrayList<>();
    private final ArrayList<String> addressArrayList = new ArrayList<>();
    private final ArrayList<String> statusArrayList = new ArrayList<>();
    private final ArrayList<String> assignedToArrayList = new ArrayList<>();
    ActivityTenantTaskBinding binding;
    private EditText searchEditText;
    private TextView noDataTextView;
    private ListView listView;
    private String emailAddress, timeStamp;
    private DatabaseReference databaseReference;
    private PostAdapter postAdapter;

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

        binding = ActivityTenantTaskBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.tenantTaskBottomNavigationView.setSelectedItemId(R.id.tenant_bottom_menu_task);

        binding.tenantTaskBottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.tenant_bottom_menu_search) {
                startActivity(new Intent(this, TenantSearchActivity.class));
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
        getPostDataFromDatabase();

        listView.setLongClickable(true);

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
                Intent intent = new Intent(this, TenantSelectedPostActivity.class);

                intent.putExtra("post", postArrayList.get(i));

                startActivity(intent);
                finish();
            });

            listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
                timeStamp = postArrayList.get(i).getTimeStamp();

                showDeleteAlert();

                return true;
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
        }, 1000);
    }

    private void showDeleteAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle("Alert!")
                .setMessage("Are you sure, you want to delete the post?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deletePost();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void deletePost() {
        String[] splitEmailAddress = emailAddress.split("\\.");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    if (timeStamp.equals(Objects.requireNonNull(dataSnapshot.child("timeStamp").getValue()).toString())) {
                        String key = Objects.requireNonNull(dataSnapshot.getKey());

                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).child(key).removeValue();
                        Toast.makeText(this, "Post Deleted!", Toast.LENGTH_LONG).show();

                        startActivity(new Intent(TenantTaskActivity.this, TenantTaskActivity.class));

                        break;
                    }
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

    private void checkSearchInData(String s) {
        if (postArrayList.size() > 0) {
            noDataTextView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            ArrayList<Post> posts = new ArrayList<>();

            for (int i = 0; i < postArrayList.size(); i++) {
                if (titleArrayList.get(i).toLowerCase().contains(s) || descriptionArrayList.get(i).toLowerCase().contains(s) || addressArrayList.get(i).toLowerCase().contains(s) || statusArrayList.get(i).toLowerCase().contains(s)) {
                    posts.add(postArrayList.get(i));
                }
            }

            PostAdapter postAdapter1 = new PostAdapter(this, R.layout.tenant_show_task_info_brief, posts);

            listView.setAdapter(postAdapter1);
        } else {
            noDataTextView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    private void getPostDataFromDatabase() {
        String[] spiltEmailAddress = emailAddress.split("\\.");

        databaseReference.child(spiltEmailAddress[0] + "-" + spiltEmailAddress[1]).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    Post post = new Post();

                    post.setAddress(Objects.requireNonNull(dataSnapshot.child("address").getValue()).toString());
                    post.setCity(Objects.requireNonNull(dataSnapshot.child("city").getValue()).toString());
                    post.setCountry(Objects.requireNonNull(dataSnapshot.child("country").getValue()).toString());
                    post.setDescription(Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString());
                    post.setEndDate(Objects.requireNonNull(dataSnapshot.child("endDate").getValue()).toString());
                    post.setEndTime(Objects.requireNonNull(dataSnapshot.child("endTime").getValue()).toString());
                    post.setLink(Objects.requireNonNull(dataSnapshot.child("link").getValue()).toString());
                    post.setStartDate(Objects.requireNonNull(dataSnapshot.child("startDate").getValue()).toString());
                    post.setStartTime(Objects.requireNonNull(dataSnapshot.child("startTime").getValue()).toString());
                    post.setState(Objects.requireNonNull(dataSnapshot.child("state").getValue()).toString());
                    post.setStatus(Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString());
                    post.setTimeStamp(Objects.requireNonNull(dataSnapshot.child("timeStamp").getValue()).toString());
                    post.setTitle(Objects.requireNonNull(dataSnapshot.child("title").getValue()).toString());
                    post.setZipCode(Objects.requireNonNull(dataSnapshot.child("zipCode").getValue()).toString());
                    post.setAssignedTo(Objects.requireNonNull(dataSnapshot.child("assignedTo").getValue()).toString());
                    post.setAssignedBy(Objects.requireNonNull(dataSnapshot.child("assignedBy").getValue()).toString());

                    titleArrayList.add(Objects.requireNonNull(dataSnapshot.child("title").getValue()).toString());
                    descriptionArrayList.add(Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString());
                    addressArrayList.add(Objects.requireNonNull(dataSnapshot.child("address").getValue()).toString());
                    statusArrayList.add(Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString());
                    assignedToArrayList.add(Objects.requireNonNull(dataSnapshot.child("assignedTo").getValue()).toString());
                    postArrayList.add(post);
                }

                postAdapter = new PostAdapter(this, R.layout.tenant_show_task_info_brief, postArrayList);

                listView.setAdapter(postAdapter);
            } else {
                showSomethingWentWrongError();
            }
        });
    }

    private void initializeAllVariables() {
        searchEditText = findViewById(R.id.tenant_task_search);
        listView = findViewById(R.id.tenant_task_list_view);
        noDataTextView = findViewById(R.id.tenant_post_no_data_text);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
    }
}