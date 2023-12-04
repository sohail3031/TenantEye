package com.example.tenanteye.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tenanteye.R;
import com.example.tenanteye.User;
import com.example.tenanteye.forgotpassword.ForgotPasswordActivity;
import com.example.tenanteye.signup.SignUpOneActivity;
import com.example.tenanteye.tenanthomepages.TenantHomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final String EMAIL_RE = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private SharedPreferences loginSharedPreference;
    private AppCompatButton signInButton, forgotPasswordButton;
    private TextView signup;
    private EditText emailAddressField, passwordField;
    private CheckBox checkBox;
    private String emailAddress, password;
    private FirebaseAuth firebaseAuth;
    private boolean isButtonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeAllVariables();

        signInButton.setOnClickListener(view -> {
            getUserDataFromUI();

            if (validateEmail() && validatePassword() && !isButtonClicked) {
                signInUser();
            }
        });

        forgotPasswordButton.setOnClickListener(view -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });

        signup.setOnClickListener(view -> {
            startActivity(new Intent(this, SignUpOneActivity.class));
        });
    }

    private void showEmailError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle("No user found!")
                .setMessage("We cannot find a user with the entered credentials. Please check them and try again!")
                .setPositiveButton(R.string.error_alert_okay, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private String getUserTypeFromDatabase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Data");
        String[] splitEmailAddress = emailAddress.split("\\.");
        User user = new User();

        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    user.setUser(Objects.requireNonNull(dataSnapshot.child("user").getValue()).toString());
                }
            } else {
                showSomethingWentWrongError();
            }
        });

        return user.getUser();
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

    private void signInUser() {
        isButtonClicked = true;

        Toast.makeText(this, "Please Wait!", Toast.LENGTH_SHORT).show();

        firebaseAuth.getCurrentUser();
        firebaseAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {
                    if (checkBox.isChecked()) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Data");
                        String[] splitEmailAddress = emailAddress.split("\\.");
                        User user = new User();

                        databaseReference.child(splitEmailAddress[0] + "-" + splitEmailAddress[1]).get().addOnCompleteListener(task1 -> {
                            if (task.isSuccessful()) {
                                for (DataSnapshot dataSnapshot : task1.getResult().getChildren()) {
                                    user.setUser(Objects.requireNonNull(dataSnapshot.child("user").getValue()).toString());

                                    Log.i("TAG", "getUserTypeFromDatabase: " + user);

                                    if (user.getUser() != null && user.getUser().length() > 0) {
                                        loginSharedPreference = getSharedPreferences("login", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = loginSharedPreference.edit();

                                        editor.putString("emailAddress", emailAddress);
                                        editor.putString("password", password);
                                        editor.putString("user", user.getUser());
                                        editor.apply();

                                        if (user.getUser().equalsIgnoreCase("tenant")) {
                                            startActivity(new Intent(LoginActivity.this, TenantHomeActivity.class));
                                            finish();
                                        } else {
                                            startActivity(new Intent(LoginActivity.this, TenantHomeActivity.class));
                                            finish();
                                        }
                                    }
                                }
                            } else {
                                isButtonClicked = false;

                                showSomethingWentWrongError();
                            }
                        });
                    }
                } else {
                    isButtonClicked = false;

                    Toast.makeText(LoginActivity.this, "You have not verified your account. Please verify it to login.", Toast.LENGTH_SHORT).show();
                }
            } else {
                isButtonClicked = false;
                
                showEmailError();
            }
        });
    }

    private boolean validatePassword() {
        if (password.isEmpty()) {
            passwordField.setError(getString(R.string.password_required));

            return false;
        } else if (password.length() <= 6) {
            passwordField.setError(getString(R.string.password_invalid));

            return false;
        } else {
            return true;
        }
    }

    private boolean validateEmail() {
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

    private void getUserDataFromUI() {
        emailAddress = emailAddressField.getText().toString();
        password = passwordField.getText().toString();
    }

    private void initializeAllVariables() {
        signInButton = findViewById(R.id.login_sign_in_button);
        forgotPasswordButton = findViewById(R.id.login_forgot_password_button);
        signup = findViewById(R.id.login_new_user_signup_text);
        emailAddressField = findViewById(R.id.login_email_field);
        passwordField = findViewById(R.id.login_password_field);
        checkBox = findViewById(R.id.login_check_box);

        firebaseAuth = FirebaseAuth.getInstance();
    }
}