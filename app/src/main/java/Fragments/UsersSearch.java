package Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.goodgameproject.R;
import com.example.goodgameproject.utilities.Account;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import Interfaces.callBackUserSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UsersSearch extends Fragment {

    private callBackUserSearch callBackUserSearch;
    private TextInputLayout searchInput;
    private MaterialButton searchButton;
    private CheckBox[] genreCheckBoxes;
    private CheckBox[] platformCheckBoxes;


    public void setCallback(callBackUserSearch callback) {
        this.callBackUserSearch = callback;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_search, container, false);
        findViews(view);
        initializeCheckBoxes(view);

        // Setup listeners for UI elements (e.g., button click)
        searchButton.setOnClickListener(v -> performSearch());

        return view;
    }



    private void findViews(View view){
        // Initialize UI elements
        searchInput = view.findViewById(R.id.search_input);
        searchButton = view.findViewById(R.id.btn_search);
    }


    private void initializeCheckBoxes(View view) {
        // Initialize game genre checkboxes
        genreCheckBoxes = new CheckBox[8];
        for (int i = 0; i < 8; i++) {
            int resID = getResources().getIdentifier("checkBox" + (i + 1), "id", getContext().getPackageName());
            genreCheckBoxes[i] = view.findViewById(resID);
        }

        // Initialize platform checkboxes
        platformCheckBoxes = new CheckBox[3];
        for (int i = 0; i < 3; i++) {
            int resID = getResources().getIdentifier("checkboxPlatform" + (i + 1), "id", getContext().getPackageName());
            platformCheckBoxes[i] = view.findViewById(resID);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveCheckboxStates();
    }

    private void saveCheckboxStates() {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (int i = 0; i < genreCheckBoxes.length; i++) {
            editor.putBoolean("genreCheckBox" + i, genreCheckBoxes[i].isChecked());
        }

        for (int i = 0; i < platformCheckBoxes.length; i++) {
            editor.putBoolean("platformCheckBox" + i, platformCheckBoxes[i].isChecked());
        }

        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreCheckboxStates();
    }

    private void restoreCheckboxStates() {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        for (int i = 0; i < genreCheckBoxes.length; i++) {
            genreCheckBoxes[i].setChecked(sharedPreferences.getBoolean("genreCheckBox" + i, false));
        }

        for (int i = 0; i < platformCheckBoxes.length; i++) {
            platformCheckBoxes[i].setChecked(sharedPreferences.getBoolean("platformCheckBox" + i, false));
        }
    }





    private void performSearch() {
        // Get the search input
        String query = String.valueOf(Objects.requireNonNull(searchInput.getEditText()).getText()).trim();
        Account.GameGenre[] selectedGenres = getSelectedGenres();
        Account.Platform[] selectedPlatforms = getSelectedPlatforms();

        // Validate the input
        if (query.isEmpty()) {
            // If no input is provided, show all users
            searchAllUsers(selectedGenres, selectedPlatforms);
        } else {
            // Proceed with the search logic
            searchUsers(query, selectedGenres, selectedPlatforms);
        }
    }



    private Account.GameGenre[] getSelectedGenres() {
        List<Account.GameGenre> selectedGenres = new ArrayList<>();
        for (CheckBox checkBox : genreCheckBoxes) {
            if (checkBox.isChecked()) {
                selectedGenres.add(Account.GameGenre.valueOf(checkBox.getText().toString().toUpperCase()));
            }
        }
        return selectedGenres.toArray(new Account.GameGenre[0]);
    }

    private Account.Platform[] getSelectedPlatforms() {
        List<Account.Platform> selectedPlatforms = new ArrayList<>();
        for (CheckBox checkBox : platformCheckBoxes) {
            if (checkBox.isChecked()) {
                selectedPlatforms.add(Account.Platform.valueOf(checkBox.getText().toString().toUpperCase()));
            }
        }
        return selectedPlatforms.toArray(new Account.Platform[0]);
    }



    private void searchAllUsers(Account.GameGenre[] selectedGenres, Account.Platform[] selectedPlatforms) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> userIds = new ArrayList<>();
                        List<Account> userAccounts = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userName = document.getString("userName");
                            String userId = document.getString("userId");
                            userIds.add(userId);

                            List<String> genreStrings = (List<String>) document.get("genres");
                            Account.GameGenre[] genreArray = genreStrings != null
                                    ? genreStrings.stream()
                                    .map(Account.GameGenre::valueOf)
                                    .toArray(Account.GameGenre[]::new)
                                    : new Account.GameGenre[0];

                            List<String> platformStrings = (List<String>) document.get("platforms");
                            Account.Platform[] platformArray = platformStrings != null
                                    ? platformStrings.stream()
                                    .map(Account.Platform::valueOf)
                                    .toArray(Account.Platform[]::new)
                                    : new Account.Platform[0];

                            if (matchesCriteria(genreArray, platformArray, selectedGenres, selectedPlatforms)) {
                                Account account = new Account(userName, genreArray, platformArray);
                                userAccounts.add(account);
                            }
                        }

                        Account[] userAccountsArray = userAccounts.toArray(new Account[0]);
                        showSearchResults(userAccountsArray, userIds);
                    } else {
                        Log.d("SearchAllUsers", "Error getting documents: ", task.getException());
                    }
                });
    }






    private void searchUsers(String query, Account.GameGenre[] selectedGenres, Account.Platform[] selectedPlatforms) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("userName", query)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> userIds = new ArrayList<>();
                        List<Account> userAccounts = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userId = document.getString("userId");
                            String userName = document.getString("userName");
                            userIds.add(userId);

                            List<String> genreStrings = (List<String>) document.get("genres");
                            Account.GameGenre[] genreArray = genreStrings != null
                                    ? genreStrings.stream()
                                    .map(Account.GameGenre::valueOf)
                                    .toArray(Account.GameGenre[]::new)
                                    : new Account.GameGenre[0];

                            List<String> platformStrings = (List<String>) document.get("platforms");
                            Account.Platform[] platformArray = platformStrings != null
                                    ? platformStrings.stream()
                                    .map(Account.Platform::valueOf)
                                    .toArray(Account.Platform[]::new)
                                    : new Account.Platform[0];

                            if (matchesCriteria(genreArray, platformArray, selectedGenres, selectedPlatforms)) {
                                Account account = new Account(userName, genreArray, platformArray);
                                userAccounts.add(account);
                            }
                        }

                        Account[] userAccountsArray = userAccounts.toArray(new Account[0]);
                        showSearchResults(userAccountsArray, userIds);
                    } else {
                        Log.d("SearchUsers", "Error getting documents: ", task.getException());
                    }
                });
    }


    private boolean matchesCriteria(Account.GameGenre[] userGenres, Account.Platform[] userPlatforms,
                                    Account.GameGenre[] selectedGenres, Account.Platform[] selectedPlatforms) {
        for (Account.GameGenre selectedGenre : selectedGenres) {
            for (Account.GameGenre userGenre : userGenres) {
                if (selectedGenre == userGenre) {
                    return true;
                }
            }
        }
        for (Account.Platform selectedPlatform : selectedPlatforms) {
            for (Account.Platform userPlatform : userPlatforms) {
                if (selectedPlatform == userPlatform) {
                    return true;
                }
            }
        }
        return false;
    }



    private String getCurrentUserId(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            return userId;
        }

        return null;
    }



    private void showSearchResults(Account[] results,List<String> userIds) {
        // Create a new instance of UserSearchResult fragment
        UserSearchResult userSearchResultFragment = new UserSearchResult();

        // Pass the search results to the fragment
        Bundle args = new Bundle();
        args.putSerializable("results", results);
        args.putString("CurrentUserId",getCurrentUserId());
        args.putStringArrayList("userIds",new ArrayList<>(userIds));

        userSearchResultFragment.setArguments(args);

        // Replace the current fragment with UserSearchResult fragment
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_results, userSearchResultFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
