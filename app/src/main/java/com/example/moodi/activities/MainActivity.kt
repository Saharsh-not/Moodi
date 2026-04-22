package com.example.moodi.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moodi.R
import com.example.moodi.adapter.CandidateAdapter
import com.example.moodi.model.Candidate

import android.view.Menu
import android.view.MenuItem
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_profile) {
            startActivity(Intent(this, ProfileActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerCandidates)
        val btnResults = findViewById<Button>(R.id.btnResults)

        // Initialize candidates in Firestore only if they don't exist
        val candidateNames = listOf("Rahul Sharma", "Priya Verma", "Aman Gupta")
        val parties = listOf("Party A", "Party B", "Party C")
        val descriptions = listOf("Focused on education", "Focused on healthcare", "Focused on jobs")
        val icons = listOf(R.drawable.symbol_party_a, R.drawable.symbol_party_b, R.drawable.symbol_party_c)

        for (i in candidateNames.indices) {
            val candidateRef = db.collection("candidates").document(candidateNames[i])
            candidateRef.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    val candidate = Candidate(candidateNames[i], parties[i], descriptions[i], 0, icons[i])
                    candidateRef.set(candidate)
                }
            }
        }

        val candidateList = listOf(
            Candidate("Rahul Sharma", "Party A", "Focused on education", 0, R.drawable.symbol_party_a),
            Candidate("Priya Verma", "Party B", "Focused on healthcare", 0, R.drawable.symbol_party_b),
            Candidate("Aman Gupta", "Party C", "Focused on jobs", 0, R.drawable.symbol_party_c)
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CandidateAdapter(candidateList)

        btnResults.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }
    }
}