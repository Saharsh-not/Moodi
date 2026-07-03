package com.example.moodi.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moodi.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResultActivity extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView tvResult1 = findViewById(R.id.tvResult1);
        TextView tvResult2 = findViewById(R.id.tvResult2);
        TextView tvResult3 = findViewById(R.id.tvResult3);

        db.collection("candidates").addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Toast.makeText(this, "Listen failed.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots) {
                    String name = doc.getString("name");
                    Long votes = doc.getLong("votes");
                    long votesVal = votes != null ? votes : 0;

                    if (name != null) {
                        switch (name) {
                            case "Rahul Sharma":
                                tvResult1.setText(name + " - " + votesVal + " votes");
                                break;
                            case "Priya Verma":
                                tvResult2.setText(name + " - " + votesVal + " votes");
                                break;
                            case "Aman Gupta":
                                tvResult3.setText(name + " - " + votesVal + " votes");
                                break;
                        }
                    }
                }
            }
        });
    }
}
