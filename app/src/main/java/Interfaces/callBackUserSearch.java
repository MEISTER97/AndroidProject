package Interfaces;

import com.example.goodgameproject.utilities.Account;

public interface callBackUserSearch {

    void searchUser(String user, Account.GameGenre[] gameGenres, Account.Platform[] platforms);

    // Method to handle successful search results
    void onSearchSuccess(Account[] results);

    // Method to handle errors
    void onSearchError(String errorMessage);

}
