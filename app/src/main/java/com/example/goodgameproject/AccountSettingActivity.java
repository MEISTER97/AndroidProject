package com.example.goodgameproject;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.goodgameproject.utilities.Account;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AccountSettingActivity extends AppCompatActivity {

    private MaterialButton saveChange;
    private CheckBox[] gamesGenres;
    private CheckBox[] platforms;
    private TextInputLayout profileNameText;
    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        findViews();

        initViews();

        loadUserProfile();
        loadCheckboxStates();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCheckboxStates(); // Load the states when the activity resumes
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveCheckboxStates();
    }

    private void findViews() {
        saveChange = findViewById(R.id.btn_saveChange);
        profileNameText = findViewById(R.id.userName);

        gamesGenres = new CheckBox[8];
        for (int i = 0; i < 8; i++) {
            int resID = getResources().getIdentifier("checkBox" + (i + 1), "id", getPackageName());
            gamesGenres[i] = findViewById(resID);
        }

        platforms = new CheckBox[3];
        for (int i = 0; i < 3; i++) {
            int resID = getResources().getIdentifier("checkboxPlatform" + (i + 1), "id", getPackageName());
            platforms[i] = findViewById(resID);
        }
    }

    private void initViews() {
        saveChange.setOnClickListener(v -> saveChangesAndMoveToMain());
    }

    private void saveCheckboxStates() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(userId);

            Map<String, Boolean> checkboxStates = new HashMap<>();
            for (int i = 0; i < gamesGenres.length; i++) {
                checkboxStates.put("gameGenre" + i, gamesGenres[i].isChecked());
            }
            for (int i = 0; i < platforms.length; i++) {
                checkboxStates.put("platform" + i, platforms[i].isChecked());
            }

            userRef.update("checkboxStates", checkboxStates)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Checkbox states successfully saved."))
                    .addOnFailureListener(e -> Log.e(TAG, "Error saving checkbox states.", e));
        }
    }

    private void loadUserProfile() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(userId);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userName = document.getString("userName");
                        if (userName != null) {
                            // Set the profile name in the TextInputLayout
                            profileNameText.getEditText().setText(userName);
                        } else {
                            profileNameText.getEditText().setText(""); // or some default value
                        }
                    } else {
                        Log.d("AccountSetting", "No such document exists for user ID: " + userId);
                    }
                } else {
                    Log.d("AccountSetting", "Error getting document for user ID: " + userId, task.getException());
                }
            });
        } else {
            // Handle the case where the user is not authenticated
            Toast.makeText(this, "User is not authenticated. Please sign in.", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadCheckboxStates() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(userId);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Boolean> checkboxStates = (Map<String, Boolean>) document.get("checkboxStates");
                        if (checkboxStates != null) {
                            for (int i = 0; i < gamesGenres.length; i++) {
                                gamesGenres[i].setChecked(checkboxStates.getOrDefault("gameGenre" + i, false));
                            }
                            for (int i = 0; i < platforms.length; i++) {
                                platforms[i].setChecked(checkboxStates.getOrDefault("platform" + i, false));
                            }
                        }
                    } else {
                        Log.d(TAG, "No such document exists for user ID: " + userId);
                        // Handle the case for the first time login, if needed
                    }
                } else {
                    Log.e(TAG, "Error getting document for user ID: " + userId, task.getException());
                }
            });
        }
    }


    private void saveChangesAndMoveToMain() {
        // Get and trim the username
        userName = String.valueOf(Objects.requireNonNull(profileNameText.getEditText()).getText()).trim();

        // Validate the username
        if (userName.isEmpty()) {
            profileNameText.setError("Username cannot be empty");
            return;
        } else {
            profileNameText.setError(null);
        }

        // Save checkbox states
        saveCheckboxStates();

        // Convert arrays to lists
        List<Account.GameGenre> selectedGenres = Arrays.asList(getSelectedGenres());
        List<Account.Platform> selectedPlatforms = Arrays.asList(getSelectedPlatforms());

        // Create a map for user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("userName", userName);
        userData.put("genres", selectedGenres);
        userData.put("platforms", selectedPlatforms);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(userId);

            // Use update to avoid overwriting the entire document
            userRef.update(userData)
                    .addOnSuccessListener(aVoid -> {
                        // Successfully written to Firestore, navigate to MainActivity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // Handle the error, show a message to the user
                        profileNameText.setError("Failed to save data. Please try again.");
                    });
        } else {
            // Handle the case where the user is not authenticated
            profileNameText.setError("User is not authenticated. Please sign in.");
        }
    }



    private Account.GameGenre[] getSelectedGenres() {
        List<Account.GameGenre> selectedGenresList = new ArrayList<>();
        for (int i = 0; i < gamesGenres.length; i++) {
            if (gamesGenres[i].isChecked()) {
                selectedGenresList.add(Account.GameGenre.values()[i]);
            }
        }
        return selectedGenresList.toArray(new Account.GameGenre[0]);
    }

    private Account.Platform[] getSelectedPlatforms() {
        List<Account.Platform> selectedPlatformsList = new ArrayList<>();
        for (int i = 0; i < platforms.length; i++) {
            if (platforms[i].isChecked()) {
                selectedPlatformsList.add(Account.Platform.values()[i]);
            }
        }
        return selectedPlatformsList.toArray(new Account.Platform[0]);
    }
}
