package com.example.goodgameproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodgameproject.utilities.CommunityAdapter;
import com.example.goodgameproject.utilities.CommunityItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityActivity extends AppCompatActivity {
    MaterialButton btn_main, btn_addGroup;
    private RecyclerView recyclerView;
    private List<CommunityItem> items;
    private CommunityAdapter communityAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextInputEditText textInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_community);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        items = new ArrayList<>();
        findViews();
        initViews();
        loadCommunityGroups();
        initRecyclerView();
    }

    private void findViews() {
        btn_main = findViewById(R.id.btn_main);
        recyclerView = findViewById(R.id.recycler_view);
        btn_addGroup = findViewById(R.id.btn_addGroup);
        textInput = findViewById(R.id.input_text);
    }

    private void initViews() {
        btn_main.setOnClickListener(v -> moveToMain());
        btn_addGroup.setOnClickListener(v -> addGroupToCommunity());

    }

    private void initRecyclerView() {
        communityAdapter = new CommunityAdapter(this, items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(communityAdapter);
    }

    private void addGroupToCommunity() {
        String groupName = String.valueOf(textInput.getText()).trim();
        if (groupName.isEmpty()) {
            Toast.makeText(this, "The Group name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Check if group name already exists
            db.collection("communityGroups")
                    .whereEqualTo("groupName", groupName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().isEmpty()) {
                            // Group name does not exist, proceed to add
                            Map<String, Object> groupData = new HashMap<>();
                            groupData.put("groupName", groupName);
                            groupData.put("userId", userId);

                            // Add group to Firestore
                            db.collection("communityGroups")
                                    .add(groupData)
                                    .addOnSuccessListener(documentReference -> {
                                        String groupId = documentReference.getId(); // Get the generated document ID
                                        CommunityItem newItem = new CommunityItem(groupName, userId, groupId);
                                        items.add(newItem);
                                        communityAdapter.notifyItemInserted(items.size() - 1);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to add group", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Group name already exists
                            Toast.makeText(this, "Group name already taken", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void loadCommunityGroups() {
        db.collection("communityGroups")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String groupName = document.getString("groupName");
                            String userId = document.getString("userId");
                            String groupId = document.getId();
                            CommunityItem item = new CommunityItem(groupName, userId,groupId);
                            items.add(item);
                        }
                        communityAdapter.notifyDataSetChanged();
                    }
                });

    }

    private void moveToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
