package com.example.tenanteye.forgotpassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tenanteye.R;
import com.example.tenanteye.login.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String EMAIL_RE = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    boolean isEmailFound = false;
    private EditText emailAddressField;
    private ImageView backImageView;
    private AppCompatButton nextButton;
    private ProgressBar progressBar;
    private String emailAddress, phoneNumber;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initializeAllVariables();

        backImageView.setOnClickListener(view -> {
            showAlertMessage();
        });

        nextButton.setOnClickListener(view -> {
            getUserData();

            if (validateEmailAddress()) {
                verifyIfEmailAddressExistsInData();

                if (isEmailFound) {
                    Intent intent = new Intent(this, ForgotPasswordSelectionActivity.class);

                    intent.putExtra("emailAddress", emailAddress);
                    intent.putExtra("phoneNumber", phoneNumber);

                    startActivity(intent);
                    finish();
                }
//                else {
//                    showErrorDialogBox();
//                }
            }
        });
    }

    private void verifyIfEmailAddressExistsInData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String[] temp = dataSnapshot.getKey().split("-");

                    if (emailAddress.equals(temp[0] + "." + temp[1])) {
                        isEmailFound = true;

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            emailAddress = dataSnapshot1.child("emailAddress").getValue().toString();
                            phoneNumber = dataSnapshot1.child("phoneNumber").getValue().toString();
                        }

                        break;
                    }
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showAlertMessage() {
        if (!emailAddress.isEmpty()) {
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

    private void getUserData() {
        emailAddress = emailAddressField.getText().toString();
    }

    private void initializeAllVariables() {
        backImageView = findViewById(R.id.forgot_password_back_arrow_image);
        nextButton = findViewById(R.id.forgot_password_next_button);
        emailAddressField = findViewById(R.id.forgot_password_email_field);
        progressBar = findViewById(R.id.forgot_password_progress_bar);
        TextView textView = findViewById(R.id.forgot_password_bottom_text_view);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Data");
    }
}