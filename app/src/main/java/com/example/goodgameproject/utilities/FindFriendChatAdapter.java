package com.example.goodgameproject.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodgameproject.AccountSettingActivity;
import com.example.goodgameproject.FriendChatActivity;
import com.example.goodgameproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

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
}
