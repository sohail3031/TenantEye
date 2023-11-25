package com.example.tenanteye.signup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tenanteye.PasswordStrength;
import com.example.tenanteye.R;
import com.example.tenanteye.login.LoginActivity;
import com.hbb20.CountryCodePicker;

import java.util.Objects;

public class SignUpThreeActivity extends AppCompatActivity {
    private static final String PASSWORD_RE = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
    TextView passwordStrengthText;
    private CountryCodePicker countryCodePicker;
    private EditText phoneNumberField, passwordField, confirmPasswordField;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private String phoneNumber, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_three);

        ImageView backImageView = findViewById(R.id.sign_up_three_back_image_view);
        AppCompatButton nextButton = findViewById(R.id.sign_up_three_next_button);
        countryCodePicker = findViewById(R.id.sign_up_three_country_code_picker);
        phoneNumberField = findViewById(R.id.sign_up_three_number_field);
        passwordField = findViewById(R.id.sign_up_three_password_field);
        confirmPasswordField = findViewById(R.id.sign_up_three_confirm_password_field);
        linearLayout = findViewById(R.id.sign_up_three_password_progress_bar_linear_layout);
        progressBar = findViewById(R.id.sign_up_three_password_progress_bar);
        passwordStrengthText = findViewById(R.id.sign_up_three_password_text_view);
//        AppCompatButton loginButton = findViewById(R.id.sign_up_three_login_button);

        backImageView.setOnClickListener(view -> {
            showAlertMessage();
        });

        nextButton.setOnClickListener(view -> {
            getUserData();

            if (validatePhoneNumber() && validatePasswords()) {
//                Bundle bundle = getIntent().getExtras();
//                Intent intent = new Intent(this, SignUpSuccessActivity.class);
//
//                assert bundle != null;
//                intent.putExtra("firstName", bundle.getString("firstName"));
//                intent.putExtra("lastName", bundle.getString("lastName"));
//                intent.putExtra("emailAddress", bundle.getString("emailAddress"));
//                intent.putExtra("gander", bundle.getString("gender"));
//                intent.putExtra("user", bundle.getString("user"));
//                intent.putExtra("dateOfBirth", bundle.getString("dateOfBirth"));
            }

            startActivity(new Intent(this, SignUpSuccessActivity.class));
            finish();
        });

        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                linearLayout.setVisibility(View.VISIBLE);
                checkPasswordStrength(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        confirmPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                linearLayout.setVisibility(View.VISIBLE);
                checkPasswordStrength(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        loginButton.setOnClickListener(view -> {
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//        });
    }

    private void getUserData() {
        countryCodePicker.registerCarrierNumberEditText(phoneNumberField);

        phoneNumber = countryCodePicker.getFullNumberWithPlus();
        password = passwordField.getText().toString();
        confirmPassword = confirmPasswordField.getText().toString();
    }

    private void showAlertMessage() {
        if (!"".equals(phoneNumber) || !"".equals(password) || !"".equals(confirmPassword)) {
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

    private boolean validatePhoneNumber() {
        if ("".equals(phoneNumber)) {
            phoneNumberField.setError(getString(R.string.phone_number_required));

            return false;
        } else if (!PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
            phoneNumberField.setError(getString(R.string.phone_number_invalid));

            return false;
        } else {
            return true;
        }
    }

    private boolean validatePasswords() {
        if (password.isEmpty()) {
            passwordField.setError(getString(R.string.password_required));

            return false;
        } else if (confirmPassword.isEmpty()) {
            confirmPasswordField.setError(getString(R.string.confirm_password_required));

            return false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordField.setError(getString(R.string.confirm_password_invalid));

            return false;
        }

        return true;
    }

    private void checkPasswordStrength(String password) {
        if (password.isEmpty()) {
            passwordStrengthText.setText("");
            progressBar.setProgress(0);

            return;
        }

        PasswordStrength passwordStrength = PasswordStrength.calculateStrength(password);

        passwordStrengthText.setText(passwordStrength.getText(this));
        passwordStrengthText.setTextColor(passwordStrength.getColor());
        progressBar.getProgressDrawable().setColorFilter(passwordStrength.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);

        if (passwordStrength.getText(this).equals("Weak")) {
            progressBar.setProgress(25);
        } else if (passwordStrength.getText(this).equals("Medium")) {
            progressBar.setProgress(50);
        } else if (passwordStrength.getText(this).equals("Strong")) {
            progressBar.setProgress(75);
        } else {
            progressBar.setProgress(100);
        }
    }
}