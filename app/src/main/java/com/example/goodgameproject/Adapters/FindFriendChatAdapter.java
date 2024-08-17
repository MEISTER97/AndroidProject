package com.example.goodgameproject.Adapters;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
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
import java.util.Map;

public class FindFriendChatAdapter extends RecyclerView.Adapter<FindFriendChatAdapter.FindFriendChatViewHolder>{
    private FirebaseAuth mAuth;
    private List<FindFriendChatItem> items;
    private Activity activity;
    private Context context;


    public FindFriendChatAdapter(Activity activity, List<FindFriendChatItem> items) {
        this.items = items;
        this.mAuth = FirebaseAuth.getInstance();
        this.activity = activity;
        this.context =activity;
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

        holder.btn_remove.setOnClickListener(v -> RemoveUserFromFriend(item.getTargetId(), position));

        loadProfileGamerbyId(item.getTargetId(), holder.imageView);

    }

    private void RemoveUserFromFriend(String friendId,int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(currentUserId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Map<String, Object>> friends = (List<Map<String, Object>>) task.getResult().get("friends");

                if (friends != null) {
                    Map<String, Object> requestToRemove = null;
                    for (Map<String, Object> request : friends) {
                        if (friendId.equals(request.get("friendId"))) {
                            requestToRemove = request;
                            break;
                        }
                    }
                    // if manage to find target friend from the current user, then remove the target user from the current user
                    if (requestToRemove != null) {
                        friends.remove(requestToRemove);
                        userRef.update("friends", friends)
                                .addOnSuccessListener(aVoid -> {
                                   // remove friend from current user

                                })
                                .addOnFailureListener(e -> Toast.makeText(context, "Error declining friend request: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                        // Remove the friend from the target user's friends
                        DocumentReference targetUserRef = db.collection("users").document(friendId);

                        targetUserRef.get().addOnCompleteListener(targetTask -> {
                            if (targetTask.isSuccessful() && targetTask.getResult() != null) {
                                List<Map<String, Object>> targetFriend = (List<Map<String, Object>>) targetTask.getResult().get("friends");

                                if (targetFriend != null) {
                                    Map<String, Object> targetFriendToRemove = null;
                                    for (Map<String, Object> request : targetFriend) {
                                        if (currentUserId.equals(request.get("friendId"))) {
                                            targetFriendToRemove = request;
                                            break;
                                        }
                                    }
                                    // if manage to find current user from the target user, then remove the current user from the target user
                                    if (targetFriendToRemove != null) {
                                        targetFriend.remove(targetFriendToRemove);
                                        targetUserRef.update("friends", targetFriend)
                                                .addOnSuccessListener(aVoid -> {
                                                    // Friend request removed from target user

                                                    if(items.isEmpty()){
                                                        Log.e(TAG, "Position out of bounds: " + position);
                                                        notifyItemRemoved(position);
                                                    }
                                                    else if(items.size()==1)
                                                    {
                                                        items.remove(position);
                                                        notifyItemRemoved(position);
                                                    }
                                                    else {
                                                        // Remove the item from the list and notify the adapter
                                                        activity.runOnUiThread(() -> {
                                                            // First remove the item from the list
                                                            items.remove(position);
                                                            // Then notify the adapter about the removal
                                                            notifyItemRemoved(position);
                                                            // Optionally notify range change if needed
                                                            notifyItemRangeChanged(position, items.size());

                                                        });

                                                    }

                                                })
                                                .addOnFailureListener(e -> Toast.makeText(context, "Error removing friend request from target user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                    } else {
                                        Toast.makeText(context, "Friend request not found for target user", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "No friend requests found for target user", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "Error fetching target user's friend requests: " + targetTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(context, "Friend request not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "No friend requests found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Error fetching friend requests: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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
        MaterialButton btn_chat,btn_remove;

        public FindFriendChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            targetUser = itemView.findViewById(R.id.targetUser);
            btn_chat = itemView.findViewById(R.id.btn_chat);
            btn_remove=itemView.findViewById(R.id.btn_remove);
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
