package com.example.goodgameproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodgameproject.R;
import com.example.goodgameproject.callBacks.callback;
import com.example.goodgameproject.utilities.Account;
import com.example.goodgameproject.Adapters.NotificationAdapter;
import com.example.goodgameproject.Items.NotificationItem;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String currentUserId;
    private MaterialButton btn_main;
    private NotificationAdapter adapter;
    private ListenerRegistration listenerRegistration;
    private List<NotificationItem> notificationItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        notificationItems = new ArrayList<>();
        btn_main = findViewById(R.id.btn_main);

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            setupFriendRequestsListener();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        setupRecyclerView();

        btn_main.setOnClickListener(v -> moveToMain());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the listener when the activity is destroyed
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    private void moveToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("NotificationActivity", "Notification items size: " + notificationItems.size());

        adapter = new NotificationAdapter(this,this, notificationItems);
        recyclerView.setAdapter(adapter);
    }



    private void setupFriendRequestsListener() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(currentUserId);

        listenerRegistration = userRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("NotificationActivity", "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                List<Map<String, Object>> friendDataList = (List<Map<String, Object>>) snapshot.get("friendRequests");
                if (friendDataList != null) {
                    notificationItems.clear(); // Clear existing items to avoid duplication
                    for (Map<String, Object> friendData : friendDataList) {
                        String friendId = (String) friendData.get("friendId");
                        if (friendId != null) {
                            fetchFriendDetails(friendId, account -> {
                                if (account != null) {
                                    notificationItems.add(new NotificationItem(account.getProfileName(), friendId));
                                    Log.d("NotificationActivity", "Adding notification item: " + account.getProfileName() + ", " + friendId);

                                    // Check if RecyclerView is already set up, because reading from firestore and passing to the adapter will always happen at the end of the activity
                                    if (adapter == null) {
                                        runOnUiThread(() -> setupRecyclerView()); // Initialize RecyclerView with data,
                                    } else {
                                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                                    }
                                }
                            });
                        }
                    }
                } else {
                    Log.d("NotificationActivity", "No friend requests found.");
                }
            } else {
                Log.d("NotificationActivity", "Current data: null");
            }
        });
    }



    private void fetchFriendDetails(String friendId, callback.AccountCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(friendId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String userName = document.getString("userName");
                    List<String> genreList = (List<String>) document.get("genres");
                    List<String> platformList = (List<String>) document.get("platforms");

                    Account.GameGenre[] genres = genreList != null ? genreList.stream().map(Account.GameGenre::valueOf).toArray(Account.GameGenre[]::new) : new Account.GameGenre[0];
                    Account.Platform[] platforms = platformList != null ? platformList.stream().map(Account.Platform::valueOf).toArray(Account.Platform[]::new) : new Account.Platform[0];

                    if (userName != null) {
                        Account account = new Account(userName, genres, platforms);
                        callback.onCallback(account);
                    } else {
                        callback.onCallback(null);
                    }
                } else {
                    Log.d("NotificationActivity", "No such document exists for user ID: " + friendId);
                    callback.onCallback(null);
                }
            } else {
                Log.d("NotificationActivity", "Error getting document for user ID: " + friendId, task.getException());
                callback.onCallback(null);
            }
        });
    }


}
