package com.example.tenanteye.signup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.tenanteye.R;
import com.example.tenanteye.login.LoginActivity;

import java.util.Objects;

public class SignUpOneActivity extends AppCompatActivity {
    private static final String NAME_RE = "^[a-zA-Z]+";
    private static final String EMAIL_RE = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private EditText firstNameField, lastNameField, emailAddressField;
    private String firstName = "", lastName = "", emailAddress = "";
    private ImageView backImageView;
    private AppCompatButton nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_one);

        initializeALlVariables();

        backImageView.setOnClickListener(view -> {
            showAlertMessage();
        });

        nextButton.setOnClickListener(view -> {
            getData();

            if (validateFirstName() && validateLastName() && validateEmailAddress()) {
                Intent intent = new Intent(this, SignUpTwoActivity.class);

                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                intent.putExtra("emailAddress", emailAddress);

                startActivity(intent);
                finish();
            }
        });
    }

    private void initializeALlVariables() {
        backImageView = findViewById(R.id.sign_up_one_back_image_view);
        nextButton = findViewById(R.id.sign_up_one_next_button_view);
        firstNameField = findViewById(R.id.sign_up_one_first_name_field);
        lastNameField = findViewById(R.id.sign_up_one_last_name_field);
        emailAddressField = findViewById(R.id.sign_up_one_email_field);
    }

    private void showAlertMessage() {
        if (!"".equals(firstName) || !"".equals(lastName) || !"".equals(emailAddress)) {
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

    private void getData() {
        firstName = firstNameField.getText().toString();
        lastName = lastNameField.getText().toString();
        emailAddress = emailAddressField.getText().toString();
    }

    private boolean validateFirstName() {
        if ("".equals(firstName)) {
            firstNameField.setError(getString(R.string.first_name_required));

            return false;
        } else if (!firstName.matches(NAME_RE)) {
            firstNameField.setError(getString(R.string.first_name_invalid));

            return false;
        }

        firstNameField.setError(null);

        return true;
    }

    private boolean validateLastName() {
        if ("".equals(lastName)) {
            lastNameField.setError(getString(R.string.last_name_required));

            return false;
        } else if (!lastName.matches(NAME_RE)) {
            lastNameField.setError(getString(R.string.last_name_invalid));

            return false;
        }

        lastNameField.setError(null);

        return true;
    }

    private boolean validateEmailAddress() {
        if ("".equals(emailAddress)) {
            emailAddressField.setError(getString(R.string.email_address_required));

            return false;
        } else if (!emailAddress.matches(EMAIL_RE)) {
            emailAddressField.setError(getString(R.string.email_address_invalid));

            return false;
        }

        emailAddressField.setError(null);

        return true;
    }
}