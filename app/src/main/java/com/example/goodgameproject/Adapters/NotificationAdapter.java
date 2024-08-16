package com.example.goodgameproject.Adapters;

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
import com.example.goodgameproject.Items.NotificationItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private final List<NotificationItem> items;
    private String targetId;
    private Activity activity;

    public NotificationAdapter(Activity activity,Context context, List<NotificationItem> items) {
        this.activity = activity;
        this.context =context;
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
        targetId= item.getTargetId();
        holder.btn_accept.setOnClickListener(v -> acceptFriend(item.getTargetId(), position));
        holder.btn_decline.setOnClickListener(v -> declineFriend(item.getTargetId(), position));

        loadProfileGamerbyId(targetId, holder.imageView);

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

        // Remove the item from the list and notify the adapter
        items.remove(position);
        notifyItemRemoved(position);
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
        // Remove the item from the list and notify the adapter
        items.remove(position);
        notifyItemRemoved(position);
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

    public NotificationItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Define the ViewHolder class
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private MaterialTextView targetUser;
        private MaterialButton btn_accept, btn_decline;


        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            targetUser = itemView.findViewById(R.id.targetUser);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            btn_decline = itemView.findViewById(R.id.btn_decline);


        }




    }



}
