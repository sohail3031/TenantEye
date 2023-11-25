package com.example.tenanteye.signup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.tenanteye.R;
import com.example.tenanteye.login.LoginActivity;

public class SignUpTwoActivity extends AppCompatActivity {
    private RadioButton genderRadioButton, userRadioButton;
    private DatePicker datePicker;
    private String gender = "", user = "", dateOfBirth = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_two);

        ImageView backImageView = findViewById(R.id.sign_up_two_back_image_view);
        AppCompatButton nextButton = findViewById(R.id.sign_up_two_next_button);
        RadioGroup genderRadioGroup = findViewById(R.id.sign_up_two_gender_radio_group);
        RadioGroup userRadioGroup = findViewById(R.id.sign_up_two_user_radio_group);
        datePicker = findViewById(R.id.sign_up_two_dob_date_picker_view);
//        AppCompatButton loginButton = findViewById(R.id.sign_up_two_login_button);

        backImageView.setOnClickListener(view -> {
            showAlertMessage();
        });

        nextButton.setOnClickListener(view -> {
            getUserData();

            if (!"".equals(gender) && !"".equals(user)) {
                Bundle bundle = getIntent().getExtras();
                Intent intent = new Intent(this, SignUpThreeActivity.class);
                assert bundle != null;

                intent.putExtra("firstName", bundle.getString("firstName"));
                intent.putExtra("lastName", bundle.getString("lastName"));
                intent.putExtra("emailAddress", bundle.getString("emailAddress"));
                intent.putExtra("gander", gender);
                intent.putExtra("user", user);
                intent.putExtra("dateOfBirth", dateOfBirth);

                startActivity(intent);
                finish();
            }
        });

        genderRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.sign_up_two_gender_male) {
                genderRadioButton = findViewById(R.id.sign_up_two_gender_male);
            } else if (i == R.id.sign_up_two_gender_female) {
                genderRadioButton = findViewById(R.id.sign_up_two_gender_female);
            } else {
                genderRadioButton = findViewById(R.id.sign_up_two_gender_other);
            }

            gender = genderRadioButton.getText().toString();
        });

        userRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.sign_up_two_user_user) {
                userRadioButton = findViewById(R.id.sign_up_two_user_user);
            } else {
                userRadioButton = findViewById(R.id.sign_up_two_user_freelancer);
            }

            user = userRadioButton.getText().toString();
        });

//        loginButton.setOnClickListener(view -> {
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//        });
    }

    private void showAlertMessage() {
        if (!"".equals(gender) || !"".equals(user)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder
                    .setTitle(R.string.sign_up_alert_title)
                    .setMessage(R.string.sign_up_alert_message)
                    .setPositiveButton(R.string.alert_yes, (dialog, which) -> {
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    })
                    .setNegativeButton(R.string.alert_no, (dialog, which) -> {
                        dialog.dismiss();
                    });

            AlertDialog alertDialog = builder.create();

            alertDialog.show();
        } else {
            finish();
        }
    }

    private void getUserData() {
        dateOfBirth = datePicker.getDayOfMonth() + "-" + datePicker.getMonth() + 1 + "-" + datePicker.getYear();
    }
}