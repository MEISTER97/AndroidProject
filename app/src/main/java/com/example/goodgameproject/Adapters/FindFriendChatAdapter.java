package com.example.goodgameproject.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodgameproject.Activities.FriendChatActivity;
import com.example.goodgameproject.R;
import com.example.goodgameproject.Items.FindFriendChatItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FindFriendChatAdapter extends RecyclerView.Adapter<FindFriendChatAdapter.FindFriendChatViewHolder>{
    private FirebaseAuth mAuth;
    private List<FindFriendChatItem> items;
    private Activity activity;


    public FindFriendChatAdapter(Activity activity, List<FindFriendChatItem> items) {
        this.items = items;
        this.mAuth = FirebaseAuth.getInstance();
        this.activity = activity;
    }

    @NonNull
    @Override
    public FindFriendChatAdapter.FindFriendChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friend_chat_item, parent, false);
        return new FindFriendChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FindFriendChatAdapter.FindFriendChatViewHolder holder, int position) {
        FindFriendChatItem item = items.get(position);
        holder.targetUser.setText(item.getTargetUser());
        holder.imageView.setImageResource(item.getImage());
        holder.btn_chat.setOnClickListener(v -> startChat(item.getTargetId(), position));

        loadProfileGamerbyId(item.getTargetId(), holder.imageView);

    }

    private void startChat(String friendId, int position) {
        Intent intent = new Intent(activity, FriendChatActivity.class);
        intent.putExtra("FRIEND_ID", friendId);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateData(List<FindFriendChatItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    // Define the ViewHolder class
    public static class FindFriendChatViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        MaterialTextView targetUser;
        MaterialButton btn_chat;

        public FindFriendChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            targetUser = itemView.findViewById(R.id.targetUser);
            btn_chat = itemView.findViewById(R.id.btn_chat);
        }
    }


    private int getDrawableForProfileNumber(int profileNumber) {
        switch (profileNumber) {
            case 0:
                return R.drawable.profilegamer0;
            case 1:
                return R.drawable.profilegamer1;
            case 2:
                return R.drawable.profilegamer2;
            case 3:
                return R.drawable.profilegamer3;
            case 4:
                return R.drawable.profilegamer4;
            case 5:
                return R.drawable.profilegamer5;
            case 6:
                return R.drawable.profilegamer6;
            case 7:
                return R.drawable.profilegamer7;
            case 8:
                return R.drawable.profilegamer8;

        }
        return R.drawable.profilegamer0;
    }

    private void loadProfileGamerbyId(String userId, ImageView imageView) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(userId);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Long profilePosLong = document.getLong("profileGamer");
                        if (profilePosLong != null) {
                            int profilePos = profilePosLong.intValue();
                            // Set the image resource based on profilePos
                            imageView.setImageResource(getDrawableForProfileNumber(profilePos));
                        } else {
                            imageView.setImageResource(getDrawableForProfileNumber(0)); // Default image
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
            Toast.makeText(activity, "User is not authenticated. Please sign in.", Toast.LENGTH_SHORT).show();
        }
    }
}
