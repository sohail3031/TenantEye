package com.example.tenanteye.forgotpassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.arch.core.executor.TaskExecutor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.tenanteye.R;
import com.example.tenanteye.login.LoginActivity;
import com.example.tenanteye.resetpassword.ResetPasswordMobileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ForgotPasswordMobileActivity extends AppCompatActivity {
    AppCompatButton verifyCodeButton;
    private ImageView backImage;
    private TextView otpTextView;
    private PinView pinView;
    private Bundle bundle;
    private String emailAddress, phoneNumber, pin, verificationID;
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks onVerificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Log.i("TAG", "onVerificationCompleted: " + phoneAuthCredential);

            String code = phoneAuthCredential.getSmsCode();

            if (code != null) {
                otpTextView.setText(code);

                verifyOTPPin(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(ForgotPasswordMobileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.i("TAG", "onVerificationFailed: " + e.getMessage());

            showErrorDialogBox();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationID = s;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_mobile);

        initializeAllVariables();
        getDataFromBundle();
        sendOTPToUser();
        setUserDataToUI();

        backImage.setOnClickListener(view -> {
            showAlertMessage();
        });

        verifyCodeButton.setOnClickListener(view -> {
            getUserDataFromUI();
        });
    }

    private void sendOTPToUser() {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder().setPhoneNumber(phoneNumber).setTimeout(60L, TimeUnit.SECONDS).setActivity(this).setCallbacks(onVerificationStateChangedCallbacks).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
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

    private void verifyOTPPin(String code) {
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationID, code);
        signInUserByCredentials(phoneAuthCredential);
    }

    private void signInUserByCredentials(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(ForgotPasswordMobileActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(ForgotPasswordMobileActivity.this, ResetPasswordMobileActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("emailAddress", emailAddress);
                    intent.putExtra("phoneNumber", phoneNumber);

                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ForgotPasswordMobileActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validateOTP() {
        if (pin.isEmpty() || pin.length() < 6) {
            pinView.setError("Invalid OTP");

            return false;
        }

        verifyOTPPin(pin);

        return true;
    }

    private void getUserDataFromUI() {
        pin = pinView.getText().toString();
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

    private void getDataFromBundle() {
        emailAddress = bundle.getString("emailAddress");
        phoneNumber = bundle.getString("phoneNumber");
    }

    @SuppressLint("SetTextI18n")
    private void setUserDataToUI() {
        otpTextView.setVisibility(View.VISIBLE);
        otpTextView.setText("An OPT has been send on your number " + phoneNumber);
    }

    private void initializeAllVariables() {
        backImage = findViewById(R.id.forgot_password_mobile_back_image_button);
        verifyCodeButton = findViewById(R.id.forgot_password_mobile_button);
        otpTextView = findViewById(R.id.forgot_password_mobile_opt_text_text_view);
        pinView = findViewById(R.id.forgot_password_mobile_opt_field);
        ProgressBar progressBar = findViewById(R.id.forgot_password_mobile_progress_bar);

        bundle = getIntent().getExtras();
    }
}