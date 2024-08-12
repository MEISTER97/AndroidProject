package com.example.goodgameproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodgameproject.callBacks.callback;
import com.example.goodgameproject.utilities.Account;
import com.example.goodgameproject.utilities.FindFriendChatAdapter;
import com.example.goodgameproject.utilities.FindFriendChatItem;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FindFriendChatActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private FindFriendChatAdapter adapter;
    private List<FindFriendChatItem> items;
    private MaterialButton btn_main;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_find_friend_chat);

        mAuth = FirebaseAuth.getInstance();
        findViews();
        initViews();
    }

    private void findViews() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btn_main = findViewById(R.id.btn_main);

        // Initialize the item list
        items = new ArrayList<>();
        adapter = new FindFriendChatAdapter(this, items);
        recyclerView.setAdapter(adapter);
    }

    private void initViews() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(userId);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get the friends list
                        List<Map<String, Object>> friendsList = (List<Map<String, Object>>) document.get("friends");
                        if (friendsList != null) {
                            List<String> acceptedFriends = new ArrayList<>();
                            for (Map<String, Object> friendMap : friendsList) {
                                Boolean accepted = (Boolean) friendMap.get("accepted");
                                String friendId = (String) friendMap.get("friendId");
                                if (Boolean.TRUE.equals(accepted) && friendId != null) {
                                    acceptedFriends.add(friendId);
                                }
                            }
                            fetchFriendDetailsForAcceptedFriends(acceptedFriends);
                        }
                    } else {
                        Log.d("FindFriendChatActivity", "No such document");
                    }
                } else {
                    Log.d("FindFriendChatActivity", "Failed to get user document", task.getException());
                }
            });
        }

        btn_main.setOnClickListener(v->moveToMain());
    }

    private void moveToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void fetchFriendDetailsForAcceptedFriends(List<String> acceptedFriends) {
        List<FindFriendChatItem> newItems = new ArrayList<>();
        for (String friendId : acceptedFriends) {
            fetchFriendDetails(friendId, account -> {
                if (account != null) {
                    // Add friend details to the list
                    newItems.add(new FindFriendChatItem(account.getProfileName(), R.drawable.profilegamer1, friendId));
                    // Update the adapter when all friends are processed
                    if (newItems.size() == acceptedFriends.size()) {
                        adapter.updateData(newItems);
                    }
                }
            });
        }
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
                    Log.d("FindFriendChatActivity", "No such document exists for user ID: " + friendId);
                    callback.onCallback(null);
                }
            } else {
                Log.d("FindFriendChatActivity", "Error getting document for user ID: " + friendId, task.getException());
                callback.onCallback(null);
            }
        });
    }
}
