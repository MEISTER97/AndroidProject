package com.example.goodgameproject.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodgameproject.Adapters.MessageGroupAdapter;
import com.example.goodgameproject.Items.MessageGroup;
import com.example.goodgameproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChatActivity extends AppCompatActivity {
    private TextView groupNameTextView;
    private MaterialButton btn_back, button_send;
    private EditText editTextMessage;
    private FirebaseAuth mAuth;
    private DatabaseReference messagesRef;
    private String groupId;
    private String groupName;
    private List<MessageGroup> messageList;
    private RecyclerView recyclerViewMessages;
    private MessageGroupAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        messageList = new ArrayList<>();

        groupName = getIntent().getStringExtra("GroupName");
        groupId = getIntent().getStringExtra("GroupId");

        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database reference
        messagesRef = FirebaseDatabase.getInstance().getReference("groupChats").child(groupId);

        findViews();
        initViews();
        loadMessages();
        setupRecyclerView();
    }

    private void findViews() {
        groupNameTextView = findViewById(R.id.groupName);
        btn_back = findViewById(R.id.btn_back);
        editTextMessage = findViewById(R.id.edit_text_message);
        button_send = findViewById(R.id.button_send);
        recyclerViewMessages = findViewById(R.id.recycler_view_messages);
    }

    private void initViews() {
        groupNameTextView.setText(groupName);
        btn_back.setOnClickListener(v -> moveToCommunity());
        button_send.setOnClickListener(v -> sendMessage());
    }

    private void moveToCommunity() {
        Intent intent = new Intent(getApplicationContext(), CommunityActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String senderId = currentUser.getUid();
            String messageId = messagesRef.push().getKey();

            getUserName(senderId, userName -> {
                long timestamp = System.currentTimeMillis();

                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("message", messageText);
                messageMap.put("senderId", senderId);
                messageMap.put("timestamp", timestamp);
                messageMap.put("senderName", userName);

                if (messageId != null) {
                    messagesRef.child(messageId).setValue(messageMap)
                            .addOnSuccessListener(aVoid -> {
                                editTextMessage.setText("");
                                // Scroll to the bottom of the RecyclerView
                                recyclerViewMessages.smoothScrollToPosition(messageList.size() - 1);
                            })
                            .addOnFailureListener(e -> Toast.makeText(GroupChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show());
                }
            });
        }
    }

    private void loadMessages() {
        // Reference to the correct group chat
        messagesRef = FirebaseDatabase.getInstance().getReference("groupChats").child(groupId);

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Data snapshot received");
                messageList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve data as a HashMap
                    Map<String, Object> messageMap = (Map<String, Object>) snapshot.getValue();

                    if (messageMap != null) {
                        // Extract fields from the map
                        String text = (String) messageMap.get("message");
                        String senderName = (String) messageMap.get("senderName");
                        String senderId = (String) messageMap.get("senderId");
                        long timestamp = messageMap.get("timestamp") != null ? (long) messageMap.get("timestamp") : 0;

                        // Create a MessageGroup instance with extracted values
                        MessageGroup message = new MessageGroup();
                        message.setText(text);
                        message.setSenderName(senderName);
                        message.setSenderId(senderId);
                        message.setTimestamp(timestamp);


                        // Add the message to the list
                        messageList.add(message);
                    }
                }

                // Sort the messageList by timestamp
                Collections.sort(messageList, new Comparator<MessageGroup>() {
                    @Override
                    public int compare(MessageGroup o1, MessageGroup o2) {
                        return Long.compare(o1.getTimestamp(), o2.getTimestamp());
                    }
                });

                messageAdapter.notifyDataSetChanged();

                // Scroll to the bottom if there are messages
                if (!messageList.isEmpty()) {
                    recyclerViewMessages.smoothScrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to load messages", databaseError.toException());
            }
        });
    }



    private void getUserName(String userId, UserNameCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String userName = (String) document.get("userName");
                    callback.onCallback(userName != null ? userName : "Unknown User");
                } else {
                    callback.onCallback("Unknown User");
                }
            } else {
                callback.onCallback("Error retrieving user");
            }
        });
    }

    private interface UserNameCallback {
        void onCallback(String userName);
    }

    private void setupRecyclerView() {
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageGroupAdapter(this, messageList, mAuth.getCurrentUser().getUid());
        recyclerViewMessages.setAdapter(messageAdapter);
    }
}

