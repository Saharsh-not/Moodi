package com.example.moodi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodi.R;
import com.example.moodi.model.Candidate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder> {

    private final List<Candidate> candidateList;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public CandidateAdapter(List<Candidate> candidateList) {
        this.candidateList = candidateList;
    }

    public static class CandidateViewHolder extends RecyclerView.ViewHolder {
        TextView name, party, description;
        Button voteBtn;
        ImageView icon;

        public CandidateViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvCandidateName);
            party = itemView.findViewById(R.id.tvParty);
            description = itemView.findViewById(R.id.tvDescription);
            voteBtn = itemView.findViewById(R.id.btnVote);
            icon = itemView.findViewById(R.id.ivCandidateIcon);
        }
    }

    @NonNull
    @Override
    public CandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_candidate, parent, false);
        return new CandidateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidateViewHolder holder, int position) {
        Candidate candidate = candidateList[position];

        holder.name.setText(candidate.getName());
        holder.party.setText(candidate.getParty());
        holder.description.setText(candidate.getDescription());
        if (candidate.getIconResId() != 0) {
            holder.icon.setImageResource(candidate.getIconResId());
        }

        holder.voteBtn.setOnClickListener(v -> {
            String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
            if (userId == null) return;

            holder.voteBtn.setEnabled(false);

            db.collection("users").document(userId).get()
                .addOnSuccessListener(document -> {
                    boolean hasVoted = document.getBoolean("hasVoted") != null && document.getBoolean("hasVoted");

                    if (!hasVoted) {
                        db.runTransaction(transaction -> {
                            var candidateRef = db.collection("candidates").document(candidate.getName());
                            var userRef = db.collection("users").document(userId);

                            var userSnapshot = transaction.get(userRef);
                            boolean stillHasNotVoted = userSnapshot.getBoolean("hasVoted") != null && userSnapshot.getBoolean("hasVoted");

                            if (!stillHasNotVoted) {
                                transaction.update(candidateRef, "votes", FieldValue.increment(1));
                                transaction.update(userRef, "hasVoted", true);
                                return null;
                            } else {
                                throw new RuntimeException("Already voted");
                            }
                        }).addOnSuccessListener(aVoid -> {
                            Toast.makeText(holder.itemView.getContext(), "Vote submitted for " + candidate.getName(), Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            holder.voteBtn.setEnabled(true);
                            String msg = e.getMessage().equals("Already voted") ? "You have already cast your vote!" : "Error: " + e.getMessage();
                            Toast.makeText(holder.itemView.getContext(), msg, Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "You have already cast your vote!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    holder.voteBtn.setEnabled(true);
                    Toast.makeText(holder.itemView.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        });
    }

    @Override
    public int getItemCount() {
        return candidateList.size();
    }
}
