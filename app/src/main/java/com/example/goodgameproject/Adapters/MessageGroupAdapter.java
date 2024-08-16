package com.example.goodgameproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodgameproject.Items.MessageGroup;
import com.example.goodgameproject.R;

import java.util.List;

public class MessageGroupAdapter extends RecyclerView.Adapter<MessageGroupAdapter.MessageViewHolder> {

    private List<MessageGroup> messageList;
    private String currentUserId;
    private Context context;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageSender;
        public TextView messageText;

        public MessageViewHolder(View view) {
            super(view);
            messageSender = view.findViewById(R.id.message_sender);
            messageText = view.findViewById(R.id.message_text);
        }
    }

    // Constructor
    public MessageGroupAdapter(Context context, List<MessageGroup> messageList, String currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        MessageGroup message = messageList.get(position);

        holder.messageText.setText(message.getText());
        holder.messageSender.setText(message.getSenderName());

        // Get color values from resources
        int color = message.getSenderId().equals(currentUserId)
                ? ContextCompat.getColor(context, R.color.Green)
                : ContextCompat.getColor(context, R.color.Orange);
        holder.messageText.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}





