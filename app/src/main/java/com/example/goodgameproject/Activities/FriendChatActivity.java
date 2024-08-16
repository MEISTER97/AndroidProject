package com.example.goodgameproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goodgameproject.Items.ChatMessage;
import com.example.goodgameproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;
import java.util.Map;

public class FriendChatActivity extends AppCompatActivity {

    private static final String TAG = "FriendChatActivity";
    private MaterialButton buttonSend, btn_back;
    private FirebaseAuth mAuth;
    private DatabaseReference chatDatabaseUser, chatDatabaseTarget;
    private ScrollView scrollViewMessages;
    private LinearLayout linearLayoutMessages;
    private EditText editTextMessage;
    private static final int MAX_MESSAGES = 30;

    String friendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_chat);
        friendId = getIntent().getStringExtra("FRIEND_ID");

        if (friendId != null) {
            Log.d(TAG, "Received FRIEND_ID: " + friendId);
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                String userId = currentUser.getUid();
                initializeDatabase(userId, friendId);
                loadMessages();
            } else {
                Log.e(TAG, "User not authenticated");
            }
        } else {
            Log.d(TAG, "No FRIEND_ID found in the Intent");
        }

        findViews();
        initViews();
    }

    private void initializeDatabase(String userId, String targetUser) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        chatDatabaseUser = database.getReference(userId + "/chats/" + targetUser);
        chatDatabaseTarget = database.getReference(targetUser + "/chats/" + userId);
    }

    private void initViews() {
        buttonSend.setOnClickListener(v -> sendMessage());
        btn_back.setOnClickListener(v -> moveBack());
    }

    private void moveBack() {
        Intent intent = new Intent(getApplicationContext(), FindFriendChatActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                String messageId = chatDatabaseUser.push().getKey(); // Generate a unique ID for the message
                ChatMessage chatMessage = new ChatMessage(messageText, userId);

                if (messageId != null) {
                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("text", chatMessage.getText());
                    messageMap.put("senderId", chatMessage.getSenderId());

                    // Save the message to both chat paths
                    chatDatabaseUser.child(messageId).setValue(messageMap);
                    chatDatabaseTarget.child(messageId).setValue(messageMap);

                    // Clear the input field
                    editTextMessage.setText("");
                }
            }
        }
    }

    private void loadMessages() {
        chatDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Data snapshot received");
                linearLayoutMessages.removeAllViews();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatMessage message = snapshot.getValue(ChatMessage.class);
                    if (message != null) {
                        String messageText = message.getText();
                        String senderId = message.getSenderId();
                        Log.d(TAG, "Message: " + messageText + " Sender: " + senderId);
                        addMessage(messageText, senderId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to load messages", databaseError.toException());
            }
        });
    }

    private void addMessage(String message, String senderId) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setPadding(10, 10, 10, 10);
        textView.setBackgroundResource(R.drawable.message_background);

        if (senderId.equals(mAuth.getCurrentUser().getUid())) {
            textView.setTextColor(getResources().getColor(R.color.Green));
        } else {
            textView.setTextColor(getResources().getColor(R.color.Orange));
        }

        linearLayoutMessages.addView(textView);

        if (linearLayoutMessages.getChildCount() > MAX_MESSAGES) {
            linearLayoutMessages.removeViewAt(0);
        }

        scrollViewMessages.post(() -> scrollViewMessages.fullScroll(View.FOCUS_DOWN));
    }

    private void findViews() {
        buttonSend = findViewById(R.id.button_send);
        scrollViewMessages = findViewById(R.id.scroll_view_messages);
        linearLayoutMessages = findViewById(R.id.linear_layout_messages);
        editTextMessage = findViewById(R.id.edit_text_message);
        btn_back = findViewById(R.id.btn_back);
    }
}
