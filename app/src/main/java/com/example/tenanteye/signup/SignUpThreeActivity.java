package com.example.tenanteye.signup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tenanteye.PasswordStrength;
import com.example.tenanteye.R;
import com.example.tenanteye.User;
import com.example.tenanteye.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.Objects;

public class SignUpThreeActivity extends AppCompatActivity {
    TextView passwordStrengthText;
    private CountryCodePicker countryCodePicker;
    private EditText phoneNumberField, passwordField, confirmPasswordField;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private String phoneNumber, password, confirmPassword, firstName, lastName, emailAddress, gender, user, dateOfBirth, country, state, city, profilePicture;
    private ImageView backImageView;
    private AppCompatButton nextButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Bundle bundle;
    private User userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_three);

        initializeAllVariables();
        getAllUserDataFromPreviousActivities();

        backImageView.setOnClickListener(view -> {
            showAlertMessage();
        });

        nextButton.setOnClickListener(view -> {
            getUserData();

            if (validatePhoneNumber() && validatePasswords()) {
                storeUserDataInUserObject();

                firebaseAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String[] splitEmailAddress = userData.getEmailAddress().split("\\.");

                        databaseReference = FirebaseDatabase.getInstance().getReference("Users Data").child(splitEmailAddress[0] + "-" + splitEmailAddress[1]);
                        databaseReference.push().setValue(userData);

                        Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(SignUpThreeActivity.this, "Your account has been created! Verify your account to login!", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(SignUpThreeActivity.this, SignUpSuccessActivity.class));
                                finish();
                            } else {
                                showErrorDialogBox();
                            }
                        });
                    } else {
                        String errorCode = Objects.requireNonNull(task.getException()).getMessage();

                        switch (Objects.requireNonNull(errorCode)) {
                            case "ERROR_EMAIL_ALREADY_IN_USE":
                                Toast.makeText(SignUpThreeActivity.this, "A user with this email already exists!", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_INVALID_EMAIL":
                                Toast.makeText(SignUpThreeActivity.this, "Email is invalid!", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_WEAK_PASSWORD":
                                Toast.makeText(SignUpThreeActivity.this, "Password is weak!", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                Toast.makeText(SignUpThreeActivity.this, "A user with this credentials already exists!", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                showErrorDialogBox();
                                break;
                        }
                    }
                });
            }
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
    }

    private void storeUserDataInUserObject() {
        userData.setFirstName(firstName);
        userData.setLastName(lastName);
        userData.setEmailAddress(emailAddress);
        userData.setCountry(country);
        userData.setState(state);
        userData.setCity(city);
        userData.setGender(gender);
        userData.setUser(user);
        userData.setDateOfBirth(dateOfBirth);
        userData.setPassword(password);
        userData.setPhoneNumber(phoneNumber);
        userData.setProfilePicture(profilePicture);

        Log.i("TAG", "storeUserDataInUserObject: " + profilePicture);
    }

    private void showErrorDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle(R.string.error_alert_title)
                .setMessage(R.string.error_alert_message)
                .setPositiveButton(R.string.error_alert_okay, (dialog, which) -> {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void initializeAllVariables() {
        backImageView = findViewById(R.id.sign_up_three_back_image_view);
        nextButton = findViewById(R.id.sign_up_three_next_button);
        countryCodePicker = findViewById(R.id.sign_up_three_country_code_picker);
        phoneNumberField = findViewById(R.id.sign_up_three_number_field);
        passwordField = findViewById(R.id.sign_up_three_password_field);
        confirmPasswordField = findViewById(R.id.sign_up_three_confirm_password_field);
        linearLayout = findViewById(R.id.sign_up_three_password_progress_bar_linear_layout);
        progressBar = findViewById(R.id.sign_up_three_password_progress_bar);
        passwordStrengthText = findViewById(R.id.sign_up_three_password_text_view);

        firebaseAuth = FirebaseAuth.getInstance();

        bundle = getIntent().getExtras();

        userData = new User();
    }

    private void getAllUserDataFromPreviousActivities() {
        firstName = bundle.getString("firstName");
        lastName = bundle.getString("lastName");
        emailAddress = bundle.getString("emailAddress");
        country = bundle.getString("country");
        state = bundle.getString("state");
        city = bundle.getString("city");
        gender = bundle.getString("gender");
        user = bundle.getString("user");
        dateOfBirth = bundle.getString("dateOfBirth");
        profilePicture = bundle.getString("profilePicture");

        Log.i("TAG", "getAllUserDataFromPreviousActivities: " + profilePicture);
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
        } else if (password.length() <= 6) {
            passwordField.setError(getString(R.string.password_invalid));

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