package com.example.moodi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moodi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        EditText aadhaar = findViewById(R.id.etAadhaar);
        EditText password = findViewById(R.id.etPassword);
        Button loginBtn = findViewById(R.id.btnLogin);
        TextView signupTv = findViewById(R.id.tvSignup);

        loginBtn.setOnClickListener(v -> {
            String aadhaarText = aadhaar.getText().toString().trim();
            String pinText = password.getText().toString().trim();

            if (aadhaarText.length() != 12) {
                aadhaar.setError("Enter 12-digit Aadhaar");
                return;
            }
            if (pinText.length() != 6) {
                password.setError("Enter 6-digit PIN");
                return;
            }

            String email = aadhaarText + "@moodi.app";

            loginBtn.setEnabled(false);
            loginBtn.setText("Authenticating...");

            auth.signInWithEmailAndPassword(email, pinText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        loginBtn.setEnabled(true);
                        loginBtn.setText("Login");
                        Exception exception = task.getException();
                        String errorMessage;
                        if (exception instanceof FirebaseAuthInvalidUserException) {
                            errorMessage = "Aadhaar not registered.";
                        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            errorMessage = "Incorrect PIN. Please check your credentials and ensure your app is registered in Firebase with the correct SHA-1 fingerprint.";
                        } else {
                            errorMessage = "Login Failed: " + (exception != null ? exception.getLocalizedMessage() : "Unknown error");
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
        });

        signupTv.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });
    }
}
