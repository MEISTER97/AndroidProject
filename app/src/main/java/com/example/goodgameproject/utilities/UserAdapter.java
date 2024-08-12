package com.example.goodgameproject.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.goodgameproject.R;
import com.example.goodgameproject.UserProfileActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

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

    @Override
    public int getItemCount() {
        return userNames.length;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView userNameTextView;
        MaterialButton btn_visitProfile;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name);
            btn_visitProfile=itemView.findViewById(R.id.btn_visitProfile);
        }
    }
}
