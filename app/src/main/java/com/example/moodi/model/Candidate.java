package com.example.moodi.model;

public class Candidate {
    private String name;
    private String party;
    private String description;
    private int votes;
    private int iconResId;

    public Candidate() {
        // Required for Firestore
    }

    public Candidate(String name, String party, String description, int votes, int iconResId) {
        this.name = name;
        this.party = party;
        this.description = description;
        this.votes = votes;
        this.iconResId = iconResId;
    }

    public String getName() { return name; }
    public String getParty() { return party; }
    public String getDescription() { return description; }
    public int getVotes() { return votes; }
    public int getIconResId() { return iconResId; }
}
