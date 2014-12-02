package com.technicallycovered.oneexperience.services;

import android.app.Notification;
import android.os.Build;
import android.service.notification.StatusBarNotification;

import java.util.ArrayList;

public class ShareableNotification {
    public final String packageName;
    public final String tickerText;
    public final ArrayList<String> actions = new ArrayList<>();
    public final long timestamp;

    public ShareableNotification(StatusBarNotification notify) {
        Notification n = notify.getNotification();
        tickerText = n.tickerText.toString();
        packageName = notify.getPackageName();
        timestamp = n.when;
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Only used when available
            for(Notification.Action action : n.actions)
            {
                actions.add(action.title.toString());
            }
        }
    }
}
