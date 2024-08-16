package com.example.goodgameproject.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodgameproject.Activities.GroupChatActivity;
import com.example.goodgameproject.R;
import com.example.goodgameproject.Items.CommunityItem;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {
    private List<CommunityItem> items;
    private Activity activity;

    public CommunityAdapter(Activity activity, List<CommunityItem> items) {
        this.items = items;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_activity_item, parent, false);
        return new CommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        CommunityItem item = items.get(position);
        holder.groupName.setText(item.getGroupName());
        holder.btn_join.setOnClickListener(v->moveToGroupChat(item));
    }

    private void moveToGroupChat(CommunityItem item){
        Intent intent = new Intent(activity, GroupChatActivity.class);
        intent.putExtra("GroupName", item.getGroupName());
        intent.putExtra("UserId", item.getHostId());
        intent.putExtra("GroupId", item.getGroupId());
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CommunityViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        MaterialButton btn_join;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupName);
            btn_join=itemView.findViewById(R.id.btn_join);
        }
    }
}
