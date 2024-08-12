package com.example.goodgameproject.utilities;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.goodgameproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private FirebaseAuth mAuth;
    private Context context;
    private List<NotificationItem> items;
    private String targetId;
    private Activity activity;


    public NotificationAdapter(Activity activity, List<NotificationItem> items) {
        this.activity = activity;
        this.items = items;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item_view, parent, false);

        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem item = items.get(position);
        holder.targetUser.setText(item.getTargetUser());
        holder.imageView.setImageResource(item.getImage());
        targetId= item.getTargetId();
        holder.btn_accept.setOnClickListener(v -> acceptFriend(targetId, position));
        holder.btn_decline.setOnClickListener(v -> declineFriend(targetId, position));

    }

    private void declineFriend(String friendId, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(currentUserId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Map<String, Object>> friendRequests = (List<Map<String, Object>>) task.getResult().get("friendRequests");

                if (friendRequests != null) {
                    Map<String, Object> requestToRemove = null;
                    for (Map<String, Object> request : friendRequests) {
                        if (friendId.equals(request.get("friendId"))) {
                            requestToRemove = request;
                            break;
                        }
                    }

                    if (requestToRemove != null) {
                        friendRequests.remove(requestToRemove);
                        userRef.update("friendRequests", friendRequests)
                                .addOnSuccessListener(aVoid -> {
                                    // Friend request declined
                                    // Remove the item from the list and notify the adapter
                                    items.remove(position);
                                    notifyItemRemoved(position);
                                })
                                .addOnFailureListener(e -> Toast.makeText(context, "Error declining friend request: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                        // Remove the request from the target user's friendRequests
                        DocumentReference targetUserRef = db.collection("users").document(friendId);

                        targetUserRef.get().addOnCompleteListener(targetTask -> {
                            if (targetTask.isSuccessful() && targetTask.getResult() != null) {
                                List<Map<String, Object>> targetFriendRequests = (List<Map<String, Object>>) targetTask.getResult().get("friends");

                                if (targetFriendRequests != null) {
                                    Map<String, Object> targetRequestToRemove = null;
                                    for (Map<String, Object> request : targetFriendRequests) {
                                        if (currentUserId.equals(request.get("friendId"))) {
                                            targetRequestToRemove = request;
                                            break;
                                        }
                                    }

                                    if (targetRequestToRemove != null) {
                                        targetFriendRequests.remove(targetRequestToRemove);
                                        targetUserRef.update("friends", targetFriendRequests)
                                                .addOnSuccessListener(aVoid -> {
                                                    // Friend request removed from target user
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


    private void acceptFriend(String friendId, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(currentUserId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Map<String, Object>> friendRequests = (List<Map<String, Object>>) task.getResult().get("friendRequests");

                if (friendRequests != null) {
                    Map<String, Object> requestToAccept = null;
                    for (Map<String, Object> request : friendRequests) {
                        if (friendId.equals(request.get("friendId"))) {
                            requestToAccept = request;
                            break;
                        }
                    }

                    if (requestToAccept != null) {
                        friendRequests.remove(requestToAccept);
                        userRef.update("friendRequests", friendRequests)
                                .addOnSuccessListener(aVoid -> {
                                    // Friend request accepted
                                    // Remove the item from the list and notify the adapter
                                    items.remove(position);
                                    notifyItemRemoved(position);
                                })
                                .addOnFailureListener(e -> Toast.makeText(context, "Error accepting friend request: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                        Map<String, Object> addFriendData = new HashMap<>();
                        addFriendData.put("friendId", friendId);
                        addFriendData.put("accepted", true);

                        // Add the friend data to the user's "friends" array
                        userRef.update("friends", FieldValue.arrayUnion(addFriendData))
                                .addOnSuccessListener(aVoid -> {
                                    // Friend added successfully
                                    Log.d(TAG, "Friend added successfully to the user's friends list.");
                                })
                                .addOnFailureListener(e -> {
                                });

                        // Update the request to 'accepted' in the target user's friends list
                        DocumentReference targetUserRef = db.collection("users").document(friendId);

                        targetUserRef.get().addOnCompleteListener(targetTask -> {
                            if (targetTask.isSuccessful() && targetTask.getResult() != null) {
                                List<Map<String, Object>> targetFriendRequests = (List<Map<String, Object>>) targetTask.getResult().get("friends");

                                if (targetFriendRequests != null) {
                                    Map<String, Object> targetRequest = null;
                                    for (Map<String, Object> request : targetFriendRequests) {
                                        if (currentUserId.equals(request.get("friendId"))) {
                                            targetRequest = request;
                                            break;
                                        }
                                    }

                                    if (targetRequest != null) {
                                        targetRequest.put("accepted", true);
                                        targetUserRef.update("friends", targetFriendRequests)
                                                .addOnSuccessListener(aVoid -> {
                                                    // Friend request updated to accepted for target user
                                                })
                                                .addOnFailureListener(e -> Toast.makeText(context, "Error updating friend request for target user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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

    public void updateData(List<NotificationItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    // Define the ViewHolder class
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        MaterialTextView targetUser;
        MaterialButton btn_accept,btn_decline;


        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            targetUser = itemView.findViewById(R.id.targetUser);
            btn_decline=itemView.findViewById(R.id.btn_decline);
            btn_accept=itemView.findViewById(R.id.btn_accept);
        }
    }
}
