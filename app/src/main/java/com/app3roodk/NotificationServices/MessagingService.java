package com.app3roodk.NotificationServices;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("MessageService", "From: " + remoteMessage.getFrom());
        Log.d("MessageService", "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Map<String, String> data = remoteMessage.getData();
        if (data.containsKey("offerId"))
            Log.e("notificationId", data.get("offerId"));
    }
}
