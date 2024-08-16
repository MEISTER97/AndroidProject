package com.example.goodgameproject.callBacks;

import com.example.goodgameproject.utilities.Account;
import com.example.goodgameproject.Items.NotificationItem;

import java.util.List;

public class callback {

    public callback() {
    }

    public interface TargetIdCallback {
        void onCallback(String targetId);
    }

    public interface NotificationItemsCallback {
        void onCallback(List<NotificationItem> notificationItems);
    }

    public interface AccountCallback {
        void onCallback(Account account);
    }

}
