package com.example.moodi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.moodi.R
import com.example.moodi.model.Candidate

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CandidateAdapter(private val candidateList: List<Candidate>) :
    RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    class CandidateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvCandidateName)
        val party: TextView = itemView.findViewById(R.id.tvParty)
        val description: TextView = itemView.findViewById(R.id.tvDescription)
        val voteBtn: Button = itemView.findViewById(R.id.btnVote)
        val icon: android.widget.ImageView = itemView.findViewById(R.id.ivCandidateIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_candidate, parent, false)
        return CandidateViewHolder(view)
    }

    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        val candidate = candidateList[position]

        holder.name.text = candidate.name
        holder.party.text = candidate.party
        holder.description.text = candidate.description
        if (candidate.iconResId != 0) {
            holder.icon.setImageResource(candidate.iconResId)
        }

        holder.voteBtn.setOnClickListener {
            val userId = auth.currentUser?.uid ?: return@setOnClickListener
            
            // Disable button immediately to prevent double-click glitch
            holder.voteBtn.isEnabled = false
            
            // Check in Firestore if THIS user has already voted
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val hasVoted = document.getBoolean("hasVoted") ?: false
                    
                    if (!hasVoted) {
                        // Use a Firestore Transaction to ensure atomicity
                        db.runTransaction { transaction ->
                            val candidateRef = db.collection("candidates").document(candidate.name)
                            val userRef = db.collection("users").document(userId)
                            
                            // Check again inside transaction to be 100% sure
                            val userSnapshot = transaction.get(userRef)
                            val stillHasNotVoted = userSnapshot.getBoolean("hasVoted") ?: false
                            
                            if (!stillHasNotVoted) {
                                // 1. Increment Candidate Vote
                                transaction.update(candidateRef, "votes", FieldValue.increment(1))
                                // 2. Mark User as having voted
                                transaction.update(userRef, "hasVoted", true)
                                null
                            } else {
                                throw Exception("Already voted")
                            }
                        }.addOnSuccessListener {
                            Toast.makeText(holder.itemView.context, "Vote submitted for ${candidate.name}", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener { e ->
                            holder.voteBtn.isEnabled = true
                            val msg = if (e.message == "Already voted") "You have already cast your vote!" else "Error: ${e.message}"
                            Toast.makeText(holder.itemView.context, msg, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(holder.itemView.context, "You have already cast your vote!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    holder.voteBtn.isEnabled = true
                    Toast.makeText(holder.itemView.context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun getItemCount(): Int = candidateList.size
}