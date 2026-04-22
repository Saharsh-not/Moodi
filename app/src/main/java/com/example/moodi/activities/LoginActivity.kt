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

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val aadhaar = findViewById<EditText>(R.id.etAadhaar)
        val password = findViewById<EditText>(R.id.etPassword)
        val loginBtn = findViewById<Button>(R.id.btnLogin)
        val signupTv = findViewById<TextView>(R.id.tvSignup)

        loginBtn.setOnClickListener {
            val aadhaarText = aadhaar.text.toString().trim()
            val pinText = password.text.toString().trim()

            if (aadhaarText.length != 12) {
                aadhaar.error = "Enter 12-digit Aadhaar"
                return@setOnClickListener
            }
            if (pinText.length != 6) {
                password.error = "Enter 6-digit PIN"
                return@setOnClickListener
            }

            val email = "${aadhaarText}@moodi.app"
            
            loginBtn.isEnabled = false
            loginBtn.text = "Authenticating..."

            auth.signInWithEmailAndPassword(email, pinText)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        loginBtn.isEnabled = true
                        loginBtn.text = "Login"
                        val exception = task.exception
                        val errorMessage = when (exception) {
                            is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Aadhaar not registered."
                            is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Incorrect PIN or SHA-1 missing."
                            else -> "Login Failed: ${exception?.localizedMessage}"
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }

        signupTv.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}