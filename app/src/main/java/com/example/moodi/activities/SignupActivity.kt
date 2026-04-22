package com.example.moodi.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moodi.R
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        val aadhaar = findViewById<EditText>(R.id.etSignupAadhaar)
        val pin = findViewById<EditText>(R.id.etSignupPassword)
        val signupBtn = findViewById<Button>(R.id.btnSignup)
        val loginTv = findViewById<TextView>(R.id.tvBackToLogin)

        loginTv.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        signupBtn.setOnClickListener {
            val aadhaarText = aadhaar.text.toString().trim()
            val pinText = pin.text.toString().trim()

            if (aadhaarText.length != 12) {
                aadhaar.error = "Aadhaar must be 12 digits"
                return@setOnClickListener
            }
            if (pinText.length != 6) {
                pin.error = "PIN must be 6 digits"
                return@setOnClickListener
            }

            val email = "${aadhaarText}@moodi.app"
            
            signupBtn.isEnabled = false
            signupBtn.text = "Registering..."

            auth.createUserWithEmailAndPassword(email, pinText)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: ""
                        val userMap = hashMapOf(
                            "aadhaar" to aadhaarText,
                            "hasVoted" to false
                        )
                        
                        db.collection("users").document(userId).set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Aadhaar Registered Successfully", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                // If Firestore fails, delete the Auth user so they can retry properly later
                                auth.currentUser?.delete()
                                signupBtn.isEnabled = true
                                signupBtn.text = "Register Aadhaar"
                                
                                val errorMsg = if (e.message?.contains("disabled") == true) {
                                    "Firestore API is DISABLED. Enable it in Google Cloud Console."
                                } else {
                                    "Database Error: ${e.message}"
                                }
                                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                            }
                    } else {
                        signupBtn.isEnabled = true
                        signupBtn.text = "Register Aadhaar"
                        
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, "Aadhaar already registered. Try Logging in.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Auth Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }
    }
}