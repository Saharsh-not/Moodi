package com.example.moodi.model

data class Candidate(
    val name: String = "",
    val party: String = "",
    val description: String = "",
    val votes: Int = 0,
    val iconResId: Int = 0 // Added this to store the drawable ID
)