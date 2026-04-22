package com.example.moodi.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.moodi.R

import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class ResultActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val tvResult1 = findViewById<TextView>(R.id.tvResult1)
        val tvResult2 = findViewById<TextView>(R.id.tvResult2)
        val tvResult3 = findViewById<TextView>(R.id.tvResult3)

        db.collection("candidates").addSnapshotListener { snapshots, e ->
            if (e != null) {
                Toast.makeText(this, "Listen failed.", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            for (doc in snapshots!!) {
                val name = doc.getString("name")
                val votes = doc.getLong("votes") ?: 0
                
                when (name) {
                    "Rahul Sharma" -> tvResult1.text = "$name - $votes votes"
                    "Priya Verma" -> tvResult2.text = "$name - $votes votes"
                    "Aman Gupta" -> tvResult3.text = "$name - $votes votes"
                }
            }
        }
    }
}