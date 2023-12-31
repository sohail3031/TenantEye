package com.example.tenanteye.freelancerhomepages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tenanteye.Post;
import com.example.tenanteye.R;
import com.example.tenanteye.login.LoginActivity;

public class FreelancerShowSelectedPostActivity extends AppCompatActivity {
    private ImageView backImageView;
    private AppCompatButton chatButton;
    private EditText title, description, address, country, state, city, zipCode, startDate, startTime, endDate, endTime, advertisementLink, postedBy, amount;
    private Post post;
    private String emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer_show_selected_post);

        SharedPreferences loginSharedPreference = getSharedPreferences("login", Context.MODE_PRIVATE);
        emailAddress = loginSharedPreference.getString("emailAddress", "");

        if (emailAddress.equals("")) {
            Toast.makeText(this, "Please login in again!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        initializeAllVariables();
        getDataFromPreviousActivity();
        showDataOnUI();

        backImageView.setOnClickListener(view -> {
            startActivity(new Intent(this, FreelancerAllTasksActivity.class));
            finish();
        });

        chatButton.setOnClickListener(view -> startChat());
    }

    private void startChat() {
        Intent intent = new Intent(this, FreelancerChatActivity.class);

        intent.putExtra("post", post);

        startActivity(intent);
        finish();
    }

    @SuppressLint("SetTextI18n")
    private void showDataOnUI() {
        title.setText(post.getTitle());
        description.setText(post.getDescription());
        address.setText(post.getAddress());
        country.setText(post.getCountry());
        state.setText(post.getState());
        city.setText(post.getCity());
        zipCode.setText(post.getZipCode());
        startDate.setText(post.getStartDate());
        startTime.setText(post.getStartTime());
        endDate.setText(post.getEndDate());
        endTime.setText(post.getEndTime());
        advertisementLink.setText(post.getLink());
        postedBy.setText(post.getAssignedBy());
        amount.setText(post.getAmount());
    }

    private void getDataFromPreviousActivity() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        post = (Post) bundle.getSerializable("post");
        assert post != null;
    }

    private void initializeAllVariables() {
        backImageView = findViewById(R.id.freelancer_show_selected_post_back_arrow_image);
        chatButton = findViewById(R.id.freelancer_show_selected_post_accept_task_button);
        title = findViewById(R.id.freelancer_show_selected_post_title_field);
        description = findViewById(R.id.freelancer_show_selected_post_description_field);
        address = findViewById(R.id.freelancer_show_selected_post_address_field);
        country = findViewById(R.id.freelancer_show_selected_post_countries_field);
        state = findViewById(R.id.freelancer_show_selected_post_states_field);
        city = findViewById(R.id.freelancer_show_selected_post_cities_field);
        startDate = findViewById(R.id.freelancer_show_selected_post_start_date_field);
        startTime = findViewById(R.id.freelancer_show_selected_post_start_time_field);
        endDate = findViewById(R.id.freelancer_show_selected_post_end_date_field);
        zipCode = findViewById(R.id.freelancer_show_selected_post_zip_code_field);
        endTime = findViewById(R.id.freelancer_show_selected_post_end_time_field);
        advertisementLink = findViewById(R.id.freelancer_show_selected_post_link_field);
        postedBy = findViewById(R.id.freelancer_show_selected_post_assigned_by_field);
        amount = findViewById(R.id.freelancer_show_selected_post_amount_field);
    }
}