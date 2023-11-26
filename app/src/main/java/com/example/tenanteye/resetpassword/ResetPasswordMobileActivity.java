package com.example.tenanteye.resetpassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tenanteye.PasswordStrength;
import com.example.tenanteye.R;
import com.example.tenanteye.forgotpassword.ForgotPasswordActivity;
import com.example.tenanteye.forgotpassword.ForgotPasswordSelectionActivity;
import com.example.tenanteye.login.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResetPasswordMobileActivity extends AppCompatActivity {
    boolean isEmailFound = false;
    private ImageView backImage;
    private AppCompatButton setNewPassword;
    private EditText passwordField, confirmPasswordField;
    private LinearLayout linearLayout;
    private TextView passwordStrengthText;
    private ProgressBar progressBar;
    private String emailAddress, password, confirmPassword;
    private Bundle bundle;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_mobile);

        initializeAllVariables();
        getUserDataFromBundle();

        backImage.setOnClickListener(view -> {
            showAlertMessage();
        });

        setNewPassword.setOnClickListener(view -> {
            linearLayout.setVisibility(View.GONE);

            getUserDataFromUI();

            if (validatePasswords()) {
                updatePassword();
            }
//            startActivity(new Intent(this, ResetPasswordSuccessActivity.class));
//            finish();
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

    private void updatePassword() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!emailAddress.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String[] temp = dataSnapshot.getKey().split("-");
                        String databaseChild = dataSnapshot.getKey().toString();

                        if (emailAddress.equals(temp[0] + "." + temp[1])) {
                            isEmailFound = true;

                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                databaseReference.child(databaseChild).child(dataSnapshot1.getKey().toString()).child("password").setValue(password);

                                startActivity(new Intent(ResetPasswordMobileActivity.this, ResetPasswordSuccessActivity.class));
                                finish();
                            }

                            break;
                        }
                    }

                    if (!isEmailFound) {
                        showErrorDialogBox();
                    }

                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showErrorDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle(R.string.no_user_with_email_alert_title)
                .setMessage(R.string.no_user_with_email_alert_message)
                .setPositiveButton(R.string.error_alert_okay, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
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

    private void getUserDataFromUI() {
        password = passwordField.getText().toString();
        confirmPassword = confirmPasswordField.getText().toString();
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

    private void getUserDataFromBundle() {
//        emailAddress = bundle.getString("emailAddress");
        emailAddress = "ahmedmohammedsohail651@gmail.com";
    }

    private void showAlertMessage() {
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
    }

    private void initializeAllVariables() {
        backImage = findViewById(R.id.reset_password_mobile_back_image_view);
        setNewPassword = findViewById(R.id.reset_password_mobile_password_set_password_button_view);
        passwordField = findViewById(R.id.reset_password_mobile_new_password_field);
        confirmPasswordField = findViewById(R.id.reset_password_mobile_confirm_password_field);
        linearLayout = findViewById(R.id.reset_password_mobile_progress_bar_linear_layout);
        passwordStrengthText = findViewById(R.id.reset_password_mobile_progressbar_text_view);
        progressBar = findViewById(R.id.reset_password_mobile_progressbar);

//        bundle = getIntent().getExtras();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Data");
    }
}