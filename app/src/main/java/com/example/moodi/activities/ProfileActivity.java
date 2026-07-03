package com.example.moodi.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moodi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView tvUserEmail, tvUserName, tvVotingStatus;
    private Button btnLogout, btnBackToVoting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();

        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvVotingStatus = findViewById(R.id.tvVotingStatus);
        btnLogout = findViewById(R.id.btnLogout);
        btnBackToVoting = findViewById(R.id.btnBackToVoting);

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail() != null ? currentUser.getEmail() : "";
            String aadhaar = email.contains("@") ? email.split("@")[0] : email;
            tvUserName.setText("Aadhaar: " + aadhaar);
            tvUserEmail.setText("Status: Verified Voter");

            // Fetch voting status from Firestore
            db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Boolean hasVoted = document.getBoolean("hasVoted");
                        if (hasVoted != null && hasVoted) {
                            tvVotingStatus.setText("VOTING STATUS: COMPLETED");
                            tvVotingStatus.setTextColor(Color.parseColor("#4CAF50"));
                        } else {
                            tvVotingStatus.setText("VOTING STATUS: PENDING");
                            tvVotingStatus.setTextColor(Color.parseColor("#F44336"));
                        }
                    } else {
                        tvVotingStatus.setText("VOTING STATUS: UNKNOWN");
                    }
                })
                .addOnFailureListener(e -> {
                    tvVotingStatus.setText("ERROR: " + e.getMessage());
                    tvVotingStatus.setTextColor(Color.parseColor("#F44336"));
                });
        } else {
            tvUserName.setText("No User Logged In");
            tvUserEmail.setText("");
            tvVotingStatus.setText("");
        }

        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnBackToVoting.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
