package com.example.goodgameproject;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private MaterialButton logoutButton;
    private MaterialButton btn_setting;
    private MaterialButton btn_SearchUser;
    private MaterialButton btn_notification;
    private MaterialButton btn_chat,btn_community;

    private FirebaseUser user;
    private MaterialTextView userDetail;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_FIRST_TIME = "first_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        findViews();
        initViews();
    }

    private void findViews() {
        logoutButton = findViewById(R.id.logoutButton);
        userDetail = findViewById(R.id.user_Name);
        btn_setting=findViewById(R.id.btn_setting);
        btn_SearchUser=findViewById(R.id.btn_searchUser);
        btn_notification=findViewById(R.id.btn_notification);
        btn_chat=findViewById(R.id.btn_chat);
        btn_community=findViewById(R.id.btn_community);
    }

    private void initViews() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        logoutButton.setOnClickListener(v -> logout());

        btn_setting.setOnClickListener(v -> moveToSetting());

        btn_SearchUser.setOnClickListener(v-> moveToSearch());

        btn_notification.setOnClickListener(v -> moveToNotification());

        btn_chat.setOnClickListener(v->moveToGoodGameChat());

        btn_community.setOnClickListener(v->moveToCommunity());
    }

    private void moveToCommunity() {
        Intent intent = new Intent(getApplicationContext(), CommunityActivity.class);
        startActivity(intent);
        finish();
    }

    private void moveToNotification() {
        Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkIfFirstTime();
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void saveUserData(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        // Create a map for user data with only the user ID
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("userId", userId); // Store the user ID

        // Save user data to Firestore
        userRef.set(userData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User data successfully saved."))
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving user data.", Toast.LENGTH_SHORT).show());
    }


    private void checkIfFirstTime() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(userId);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (!task.getResult().exists()) {
                        saveUserData(userId);
                        Toast.makeText(this, "Welcome to the app! This is your first time here.", Toast.LENGTH_LONG).show();
                        moveToSetting();

                    } else {
                        loadUserData(userId);
                    }
                } else {
                    Toast.makeText(this, "Error checking user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadUserData(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String userName = document.getString("userName");
                    if (userName != null) {
                        userDetail.setText("Hello "+ userName);
                    } else {
                        userDetail.setText("Username not found.");
                    }
                } else {
                    Log.d(TAG, "No such document exists for user ID: " + userId);
                    userDetail.setText("User data not found.");
                }
            } else {
                Log.d(TAG, "Error getting document for user ID: " + userId, task.getException());
                userDetail.setText("Error loading user data.");
            }
        });
    }

    private void moveToGoodGameChat(){
        Intent intent = new Intent(getApplicationContext(), FindFriendChatActivity.class);
        startActivity(intent);
        finish();
    }


    private void moveToSetting(){
        Intent intent = new Intent(getApplicationContext(), AccountSettingActivity.class);
        startActivity(intent);
        finish();
    }

    private void moveToSearch() {
        Intent intent = new Intent(getApplicationContext(), UserSearchActivity.class);
        startActivity(intent);
        finish();
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
