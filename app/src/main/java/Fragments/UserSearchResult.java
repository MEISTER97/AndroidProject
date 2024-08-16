package Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.goodgameproject.R;
import com.example.goodgameproject.utilities.Account;
import com.example.goodgameproject.Adapters.UserAdapter;
import java.util.ArrayList;
import java.util.List;

public class UserSearchResult extends Fragment {
    private Account[] accounts;
    private String[] names;
    private Account.GameGenre[][] genres;
    private Account.Platform[][] platforms;
    private String currentUserId;
    private ArrayList<String> userIds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            accounts = (Account[]) getArguments().getSerializable("results");
            currentUserId = getArguments().getString("CurrentUserId");
            userIds = getArguments().getStringArrayList("userIds");

            Log.d("UserSearchResult", "Current User ID: " + currentUserId);
            Log.d("UserSearchResult", "User IDs: " + userIds.toString());

            // Extract names, genres, and platforms from accounts
            List<String> nameList = new ArrayList<>();
            List<Account.GameGenre[]> genreList = new ArrayList<>();
            List<Account.Platform[]> platformList = new ArrayList<>();

            if (accounts != null) {
                for (Account account : accounts) {
                    if (account != null) {
                        // Collect names
                        nameList.add(account.getProfileName());

                        // Collect genres and platforms
                        genreList.add(account.getSelectedGenres());
                        platformList.add(account.getSelectedPlatforms());
                    }
                }
            }

            // Convert lists to arrays
            names = nameList.toArray(new String[0]);
            genres = genreList.toArray(new Account.GameGenre[0][]);
            platforms = platformList.toArray(new Account.Platform[0][]);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_search_result, container, false);

        // Set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Check if search results are not null
        if (names != null) {
            UserAdapter adapter = new UserAdapter(getActivity(), names,genres,platforms,currentUserId,userIds );
            recyclerView.setAdapter(adapter);

        }

        return view;
    }

    // Method to update search results (can be called from other fragments or activities)
    public void updateSearchResults(String[] results) {
        this.names = results;
        // Update the RecyclerView with new data
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_view);
        UserAdapter adapter = new UserAdapter(getActivity(), names,genres,platforms,currentUserId,userIds );
        recyclerView.setAdapter(adapter);
    }
}
