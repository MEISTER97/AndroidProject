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
import com.example.goodgameproject.R;
import com.example.goodgameproject.Activities.UserProfileActivity;
import com.example.goodgameproject.utilities.Account;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private String[] userNames;  // Array of user names
    private Activity activity; // Use Activity instead of Context
    private Account account;
    private Account.GameGenre[][] gamesGenres;
    private Account.Platform[][] platforms;
    private String currentUserId;
    private ArrayList<String> userIds;

    public UserAdapter(Activity activity, String[] userNames,Account.GameGenre[][] gamesGenres,Account.Platform[][] platforms,String currentUserId,ArrayList<String> userIds) {
        this.activity = activity;
        this.userNames = userNames;
        this.gamesGenres=gamesGenres;
        this.platforms=platforms;
        this.currentUserId=currentUserId;
        this.userIds=userIds;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String userName = userNames[position];
        Account.GameGenre[] genre = gamesGenres[position];
        Account.Platform[] platform = platforms[position];
        String targetUserId = userIds.get(position);

        holder.userNameTextView.setText("User " + userName);

        loadProfileGamerbyId(targetUserId, holder.imageView);

        holder.btn_visitProfile.setOnClickListener(v -> {
            // Ensure context is not null
            if (activity  != null) {
                Intent intent = new Intent(activity , UserProfileActivity.class);
                account = new Account(userName,genre,platform);
                intent.putExtra("ACCOUNT", account);
                intent.putExtra("CURRENT_USER_ID", currentUserId);
                intent.putExtra("TARGET_USER_ID", targetUserId);
                activity .startActivity(intent);
                activity.finish();
            } else {
                // Handle case where context is null
                Log.e("UserAdapter", "Context is null, cannot start activity");
            }
        });


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

    @Override
    public int getItemCount() {
        return userNames.length;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView userNameTextView;
        MaterialButton btn_visitProfile;
        ImageView imageView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name);
            btn_visitProfile=itemView.findViewById(R.id.btn_visitProfile);
            imageView=itemView.findViewById(R.id.imageView);
        }
    }
}
