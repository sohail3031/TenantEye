package com.example.tenanteye.freelancerhomepages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.tenanteye.tenanthomepages.TenantMoreActivity;
import com.example.tenanteye.tenanthomepages.TenantUpdatePasswordActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class FreelancerUpdatePasswordActivity extends AppCompatActivity {
    private ImageView backImageView;
    private EditText currentPasswordField, passwordField, confirmPasswordField;
    private AppCompatButton button;
    private LinearLayout linearLayout;
    private TextView passwordStrengthText;
    private ProgressBar progressBar;
    private String currentPassword, password, confirmPassword, emailAddress, previousPassword;
    private User user;
    private boolean isButtonClicked = false, isEmailFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer_update_password);

        initializeAllVariables();
        getDataFromPreviousActivity();

        button.setOnClickListener(view -> {
            linearLayout.setVisibility(View.GONE);

            getDataFromUI();

            if (!currentPassword.equals(password)) {
                if (Objects.equals(currentPassword, previousPassword)) {
                    if (validatePasswords() && !isButtonClicked) {
                        updatePassword();
                    }
                } else {
                    showInvalidCurrentPassword();
                }
            } else {
                showSamePasswordErrorPassword();
            }
        });

        backImageView.setOnClickListener(view -> showAlertMessage());

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

    private void showAlertMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle(R.string.sign_up_alert_title)
                .setMessage(R.string.sign_up_alert_message)
                .setPositiveButton(R.string.alert_yes, (dialog, which) -> {
                    startActivity(new Intent(this, FreelancerMoreActivity.class));
                    finish();
                })
                .setNegativeButton(R.string.alert_no, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void showSamePasswordErrorPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle("Invalid Password!")
                .setMessage("Current password and new password should not be same!")
                .setPositiveButton(R.string.error_alert_okay, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void showInvalidCurrentPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle("Invalid Password!")
                .setMessage("Current password is wrong!")
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

    private void showValidationError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle(R.string.error_alert_title)
                .setMessage(R.string.error_alert_message)
                .setPositiveButton(R.string.error_alert_okay, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
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

    private void updatePassword() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Data");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!emailAddress.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
//
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String[] temp = Objects.requireNonNull(dataSnapshot.getKey()).split("-");
                        String databaseChild = dataSnapshot.getKey();

                        if (emailAddress.equals(temp[0] + "." + temp[1])) {
                            isEmailFound = true;

                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                previousPassword = Objects.requireNonNull(dataSnapshot1.child("password").getValue()).toString();
                                databaseReference.child(databaseChild).child(Objects.requireNonNull(dataSnapshot1.getKey())).child("password").setValue(password);

                                AuthCredential authCredential = EmailAuthProvider.getCredential(emailAddress, previousPassword);
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                assert firebaseUser != null;
                                firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(FreelancerUpdatePasswordActivity.this, "Password Updated!", Toast.LENGTH_SHORT).show();

                                                    SharedPreferences loginSharedPreference = getSharedPreferences("login", Context.MODE_PRIVATE);

                                                    loginSharedPreference.edit().clear().apply();

                                                    startActivity(new Intent(FreelancerUpdatePasswordActivity.this, LoginActivity.class));
                                                    finish();
                                                } else {
                                                    showValidationError();
                                                }
                                            }
                                        });
                                    }
                                });
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

    private void getDataFromUI() {
        currentPassword = currentPasswordField.getText().toString();
        password = passwordField.getText().toString();
        confirmPassword = confirmPasswordField.getText().toString();
    }

    private void getDataFromPreviousActivity() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        user = (User) bundle.getSerializable("user");
        assert user != null;
        emailAddress = user.getEmailAddress();
        previousPassword = user.getPassword();
    }

    private void initializeAllVariables() {
        backImageView = findViewById(R.id.freelancer_update_password_back_image_view);
        currentPasswordField = findViewById(R.id.freelancer_update_password_current_password_field);
        passwordField = findViewById(R.id.freelancer_update_password_new_password_field);
        confirmPasswordField = findViewById(R.id.freelancer_update_password_confirm_password_field);
        button = findViewById(R.id.freelancer_update_password_set_password_button_view);
        linearLayout = findViewById(R.id.freelancer_update_password_progress_bar_linear_layout);
        passwordStrengthText = findViewById(R.id.freelancer_update_password_progressbar_text_view);
        progressBar = findViewById(R.id.freelancer_update_password_progressbar);
    }
}