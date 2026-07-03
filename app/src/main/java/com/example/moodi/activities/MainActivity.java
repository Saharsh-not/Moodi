package com.example.moodi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodi.R;
import com.example.moodi.adapter.CandidateAdapter;
import com.example.moodi.model.Candidate;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerCandidates);
        Button btnResults = findViewById(R.id.btnResults);

        // Initialize candidates in Firestore only if they don't exist
        String[] candidateNames = {"Rahul Sharma", "Priya Verma", "Aman Gupta"};
        String[] parties = {"Party A", "Party B", "Party C"};
        String[] descriptions = {"Focused on education", "Focused on healthcare", "Focused on jobs"};
        int[] icons = {R.drawable.symbol_party_a, R.drawable.symbol_party_b, R.drawable.symbol_party_c};

        for (int i = 0; i < candidateNames.length; i++) {
            final int index = i;
            DocumentReference candidateRef = db.collection("candidates").document(candidateNames[i]);
            candidateRef.get().addOnSuccessListener(document -> {
                if (!document.exists()) {
                    Candidate candidate = new Candidate(candidateNames[index], parties[index], descriptions[index], 0, icons[index]);
                    candidateRef.set(candidate);
                }
            });
        }

        List<Candidate> candidateList = new ArrayList<>();
        candidateList.add(new Candidate("Rahul Sharma", "Party A", "Focused on education", 0, R.drawable.symbol_party_a));
        candidateList.add(new Candidate("Priya Verma", "Party B", "Focused on healthcare", 0, R.drawable.symbol_party_b));
        candidateList.add(new Candidate("Aman Gupta", "Party C", "Focused on jobs", 0, R.drawable.symbol_party_c));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CandidateAdapter(candidateList));

        btnResults.setOnClickListener(v -> {
            startActivity(new Intent(this, ResultActivity.class));
        });
    }
}
