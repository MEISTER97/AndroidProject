package com.example.goodgameproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.goodgameproject.utilities.Account;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private MaterialTextView userNameTextView;
    private MaterialTextView genresTextView;
    private MaterialTextView platformsTextView;
    private Account account;
    private MaterialButton btn_back;
    private MaterialButton btn_addFriend;
    private Context context;
    private String currentUserId;
    private String targetUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        findViews();
        initViews();

    }

    private void initViews() {
        // Get the Account object from the Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("ACCOUNT")) {
            account = (Account) intent.getSerializableExtra("ACCOUNT");
            currentUserId = intent.getStringExtra("CURRENT_USER_ID");
            targetUserId = intent.getStringExtra("TARGET_USER_ID");


            // Log the received data
            Log.d("UserProfileActivity", "Account: " + account.getProfileName());
            Log.d("UserProfileActivity", "Current User ID: " + currentUserId);
            Log.d("UserProfileActivity", "Target User ID: " + targetUserId);

            if (account != null) {
                // Display account details
                userNameTextView.setText("User name " + account.getProfileName());

                // Display genres
                String[] genres = convertGenresToStringArray(account.getSelectedGenres());
                genresTextView.setText(String.join(", ", genres));

                // Display platforms
                String[] platforms = convertPlatformsToStringArray(account.getSelectedPlatforms());
                platformsTextView.setText(String.join(", ", platforms));
            } else {
                userNameTextView.setText("Account data not available");
                genresTextView.setText("Genres not available");
                platformsTextView.setText("Platforms not available");
            }
        }
        btn_back.setOnClickListener(v -> moveToSearch());
        btn_addFriend.setOnClickListener(v-> addFriendToUser(currentUserId,targetUserId));

    }

    private void addFriendToUser(String userId, String friendId) {
        if (checkIfSameUser(userId, friendId)) {
            Toast.makeText(this, "You can't add yourself as friend!", Toast.LENGTH_SHORT).show();
            return;
        }

        checkIfNotAlreadyFriend(userId, friendId, isAlreadyFriend -> {

            if (isAlreadyFriend) {
                Toast.makeText(this, "You have already sent a request.", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userRef = db.collection("users").document(userId);

                // Create a map to store the friend ID and acceptance status
                Map<String, Object> friendData = new HashMap<>();
                friendData.put("friendId", friendId);
                friendData.put("accepted", false);

                // Add the friend data to the user's "friends" array
                userRef.update("friends", FieldValue.arrayUnion(friendData))
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Friend request sent successfully.", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error sending friend request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                DocumentReference friendRef = db.collection("users").document(friendId);

                // Create a map to store in the friend data
                Map<String, Object> userData = new HashMap<>();
                userData.put("friendId", userId);


                // Add the friend data to the user's "friends" array
                friendRef.update("friendRequests", FieldValue.arrayUnion(userData))
                        .addOnSuccessListener(aVoid -> {
                            // Successfully added friend request for friend
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error adding friend request for friend: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            }


        });
    }

    private boolean checkIfSameUser(String userId, String friendId) {
        if (userId == null || friendId == null) {
            return false;
        }
        return userId.equals(friendId);
    }


    private void checkIfNotAlreadyFriend(String userId, String friendId, OnFriendCheckCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Retrieve the friends field, which is an ArrayList
                    List<Map<String, Object>> friendsList = (List<Map<String, Object>>) document.get("friends");

                    boolean isAlreadyFriend = false;
                    if (friendsList != null) {
                        for (Map<String, Object> friend : friendsList) {
                            // Check if the friendId is in the friends list
                            if (friendId.equals(friend.get("friendId"))) {
                                isAlreadyFriend = true;
                                break;
                            }
                        }
                    }
                    listener.onCheckComplete(isAlreadyFriend);
                } else {
                    listener.onCheckComplete(false); // Document does not exist
                }
            } else {
                Log.d("checkIfNotAlreadyFriend", "Error getting documents: ", task.getException());
                listener.onCheckComplete(false); // Task failed
            }
        });
    }



    interface OnFriendCheckCompleteListener {
        void onCheckComplete(boolean isAlreadyFriend);
    }

    private void moveToSearch() {
        Intent intent = new Intent(getApplicationContext(), UserSearchActivity.class);
        startActivity(intent);
        finish();
    }

    private void findViews(){
        // Initialize TextViews
        userNameTextView = findViewById(R.id.user_name_text_view);
        genresTextView = findViewById(R.id.games_genres);
        platformsTextView = findViewById(R.id.games_platforms);
        btn_back=findViewById(R.id.btn_back);
        btn_addFriend=findViewById(R.id.btn_addFriend);
    }

    // Method to convert GameGenre array to String array
    private String[] convertGenresToStringArray(Account.GameGenre[] genres) {
        if (genres == null) return new String[0];
        String[] genreStrings = new String[genres.length];
        for (int i = 0; i < genres.length; i++) {
            genreStrings[i] = genres[i].toString(); // Convert enum to string
        }
        return genreStrings;
    }

    // Method to convert Platform array to String array
    private String[] convertPlatformsToStringArray(Account.Platform[] platforms) {
        if (platforms == null) return new String[0];
        String[] platformStrings = new String[platforms.length];
        for (int i = 0; i < platforms.length; i++) {
            platformStrings[i] = platforms[i].toString(); // Convert enum to string
        }
        return platformStrings;
    }
}
