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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        EditText aadhaar = findViewById(R.id.etSignupAadhaar);
        EditText pin = findViewById(R.id.etSignupPassword);
        Button signupBtn = findViewById(R.id.btnSignup);
        TextView loginTv = findViewById(R.id.tvBackToLogin);

        loginTv.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        signupBtn.setOnClickListener(v -> {
            String aadhaarText = aadhaar.getText().toString().trim();
            String pinText = pin.getText().toString().trim();

            if (aadhaarText.length() != 12) {
                aadhaar.setError("Aadhaar must be 12 digits");
                return;
            }
            if (pinText.length() != 6) {
                pin.setError("PIN must be 6 digits");
                return;
            }

            String email = aadhaarText + "@moodi.app";

            signupBtn.setEnabled(false);
            signupBtn.setText("Registering...");

            auth.createUserWithEmailAndPassword(email, pinText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("aadhaar", aadhaarText);
                        userMap.put("hasVoted", false);

                        db.collection("users").document(userId).set(userMap)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Aadhaar Registered Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                if (auth.getCurrentUser() != null) {
                                    auth.getCurrentUser().delete();
                                }
                                signupBtn.setEnabled(true);
                                signupBtn.setText("Register Aadhaar");

                                String errorMsg = e.getMessage() != null && e.getMessage().contains("disabled") ?
                                    "Firestore API is DISABLED. Enable it in Google Cloud Console." :
                                    "Database Error: " + e.getMessage();
                                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                            });
                    } else {
                        signupBtn.setEnabled(true);
                        signupBtn.setText("Register Aadhaar");

                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, "Aadhaar already registered. Try Logging in.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Auth Error: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                        }
                    }
                });
        });
    }
}
