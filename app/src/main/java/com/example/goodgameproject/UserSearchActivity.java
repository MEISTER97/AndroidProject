package com.example.goodgameproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.goodgameproject.utilities.Account;
import com.google.android.material.button.MaterialButton;
import Interfaces.callBackUserSearch;
import Fragments.UserSearchResult;
import Fragments.UsersSearch;

public class UserSearchActivity extends AppCompatActivity implements callBackUserSearch{
    private MaterialButton mainButton;
    private FrameLayout usersSearch_Frame_list;
    private FrameLayout usersSearchInfo_Frame_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        findViews();
        initViews();

        // Load the UsersSearch fragment into the fragment_container_search
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            UsersSearch usersSearchFragment = new UsersSearch();
            usersSearchFragment.setCallback(this); // Set the callback
            fragmentTransaction.add(R.id.fragment_container_search, usersSearchFragment);
            fragmentTransaction.commit();
        }


    }

    private void findViews() {
        mainButton = findViewById(R.id.btn_Main);
        usersSearch_Frame_list = findViewById(R.id.fragment_container_search);
        usersSearchInfo_Frame_list = findViewById(R.id.fragment_container_results);
    }

    private void initViews() {
        mainButton.setOnClickListener(v -> moveToMain());
    }

    private void moveToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onSearchSuccess(Account[] results) {
        // Create a new instance of UserSearchResult fragment
        UserSearchResult userSearchResultFragment = new UserSearchResult();

        // Pass the search results to the fragment
        Bundle args = new Bundle();
        args.putSerializable("results", results);
        userSearchResultFragment.setArguments(args);

        // Replace the current fragment with UserSearchResult fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_results, userSearchResultFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public void onSearchError(String errorMessage) {
        // Handle search error (e.g., show a toast or dialog)
    }

    @Override
    public void searchUser(String user, Account.GameGenre[] gameGenres, Account.Platform[] platforms) {


        // Simulated search operation
        Account[] searchResults = {}; // Replace with actual search logic

        // Notify the fragment with the results
        onSearchSuccess(searchResults);
    }

}
