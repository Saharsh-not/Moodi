package com.example.moodi.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.moodi.R
import android.graphics.Color
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvVotingStatus: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnBackToVoting: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()

        tvUserName = findViewById(R.id.tvUserName)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        tvVotingStatus = findViewById(R.id.tvVotingStatus)
        btnLogout = findViewById(R.id.btnLogout)
        btnBackToVoting = findViewById(R.id.btnBackToVoting)

        val currentUser = auth.currentUser

        if (currentUser != null) {
            val email = currentUser.email ?: ""
            val aadhaar = email.substringBefore("@")
            tvUserName.text = "Aadhaar: $aadhaar"
            tvUserEmail.text = "Status: Verified Voter"
            
            // Fetch voting status from Firestore
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val hasVoted = document.getBoolean("hasVoted") ?: false
                        if (hasVoted) {
                            tvVotingStatus.text = "VOTING STATUS: COMPLETED"
                            tvVotingStatus.setTextColor(Color.parseColor("#4CAF50"))
                        } else {
                            tvVotingStatus.text = "VOTING STATUS: PENDING"
                            tvVotingStatus.setTextColor(Color.parseColor("#F44336"))
                        }
                    } else {
                        tvVotingStatus.text = "VOTING STATUS: UNKNOWN"
                    }
                }
                .addOnFailureListener { e ->
                    tvVotingStatus.text = "ERROR: ${e.message}"
                    tvVotingStatus.setTextColor(Color.parseColor("#F44336"))
                }
        } else {
            tvUserName.text = "No User Logged In"
            tvUserEmail.text = ""
            tvVotingStatus.text = ""
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btnBackToVoting.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}