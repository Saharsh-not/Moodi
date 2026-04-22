# 🗳️ Moodi — Aadhaar-Based Mobile E-Voting App

**Moodi** is a secure, Aadhaar-linked Android e-voting application that allows verified Indian citizens to register, authenticate, and cast their vote — all from their mobile phone. Built with Kotlin and powered by Google Firebase.

---

## 📱 Screenshots

| Login | Register | Vote | Profile |
|-------|----------|------|---------|
| Aadhaar + PIN login | Secure voter enrolment | Choose your candidate | Voting status dashboard |

---

## ✨ Features

- 🔐 **Aadhaar-based Authentication** — Unique 12-digit Aadhaar number used as voter identity
- 🔑 **6-digit PIN login** — Secure PIN processed via Firebase Authentication
- 🗳️ **One-vote enforcement** — Firestore tracks `hasVoted` flag; duplicate voting is impossible
- 📊 **Live Results** — Real-time vote counts via Firestore snapshot listeners
- 👤 **Voter Profile Dashboard** — Shows Aadhaar identity and voting completion status
- 🚪 **Session Management** — Persistent login with Firebase Auth; logout clears session

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| IDE | Android Studio |
| Min SDK | API 24 (Android 7.0) |
| Target SDK | API 35 (Android 15) |
| Authentication | Firebase Authentication |
| Database | Cloud Firestore |
| UI | Material Design 3 — RecyclerView, CardView, ConstraintLayout |
| Build | Gradle with Kotlin DSL (`.kts`) |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio (Ladybug or newer)
- JDK 11+
- A Google Firebase project
- Git

### 1. Clone the Repository

```bash
git clone https://github.com/Saharsh-not/Moodi.git
cd moodi
```

### 2. Firebase Setup

1. Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
2. Register your Android app with package name `com.example.moodi`.
3. Download the `google-services.json` file and place it inside the `app/` directory.
4. In the Firebase Console, enable:
   - **Authentication → Email/Password** sign-in provider
   - **Cloud Firestore** (start in test mode for development)

### 3. Open in Android Studio

1. Open Android Studio → **File → Open** → select the cloned project folder.
2. Let Gradle sync complete.
3. Connect a device or start an emulator (API 24+).
4. Click **Run ▶** or press `Shift + F10`.

---

## 🗂️ Project Structure

```
Moodi/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/moodi/
│   │   │   ├── MainActivity.kt          # Login screen
│   │   │   ├── SignUpActivity.kt         # Voter registration
│   │   │   ├── VotingActivity.kt         # Candidate list & voting
│   │   │   ├── ResultActivity.kt         # Live vote results
│   │   │   └── ProfileActivity.kt        # Voter profile & status
│   │   └── res/
│   │       ├── layout/                   # XML layout files
│   │       └── drawable/                 # Icons and assets
│   ├── google-services.json              # Firebase config (not committed)
│   └── build.gradle.kts                  # App-level Gradle config
├── build.gradle.kts                      # Project-level Gradle config
└── README.md
```

---

## 🔥 Firestore Data Model

### `users` collection

```
users/
└── {firebaseUID}/
    ├── aadhaar     : String   // 12-digit Aadhaar number
    ├── hasVoted    : Boolean  // false initially; true after voting
    └── votedFor    : String   // candidate name; empty initially
```

### `candidates` collection

```
candidates/
└── {candidateId}/
    ├── name        : String   // e.g. "Rahul Sharma"
    ├── party       : String   // e.g. "Party A"
    ├── focus       : String   // e.g. "Focused on education"
    └── voteCount   : Number   // incremented atomically on each vote
```

---

## 🔐 How Authentication Works

Moodi doesn't expose Aadhaar numbers directly as login credentials. Instead:

1. The Aadhaar number is converted to a synthetic email: `<aadhaar>@moodi.vote`
2. The 6-digit PIN is used as the Firebase Auth password
3. Firebase handles hashing, session tokens (JWT), and rate limiting
4. On login, the UID is used to query the user's Firestore profile

This keeps Aadhaar data off the authentication layer while still using it as a unique identifier.

---

## 🧱 Dependencies

```kotlin
// Firebase
implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
implementation("com.google.firebase:firebase-auth")
implementation("com.google.firebase:firebase-firestore")

// UI
implementation("androidx.recyclerview:recyclerview:1.3.2")
implementation("androidx.cardview:cardview:1.0.0")
implementation(libs.material)
implementation(libs.constraintlayout)
```

---

## ⚠️ Important Notes

- **Do not commit `google-services.json`** to a public repository — it contains your Firebase project credentials. Add it to `.gitignore`.
- The current Firestore rules are in test mode. Before production use, configure proper [security rules](https://firebase.google.com/docs/firestore/security/get-started).
- This project is a proof-of-concept for academic purposes. Production e-voting systems require additional layers of security including OTP-based Aadhaar verification via UIDAI APIs.

---

## 👨‍💻 Author

**Saharsh Tiwari**  
Registration No: 235811276 | Roll No: 25  
Manipal Institute of Technology  
Advance Technology Lab

---

## 📄 License

This project is intended for academic use only.
