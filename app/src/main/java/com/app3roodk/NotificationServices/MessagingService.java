package com.app3roodk.NotificationServices;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.app3roodk.Constants;
import com.app3roodk.R;
import com.app3roodk.Schema.CustomNotification;
import com.app3roodk.UI.DetailActivity.DetailActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString("NOTIF", remoteMessageJson);
//        editor.apply();

        final CustomNotification customNotification = new CustomNotification();
        customNotification.setTitle(remoteMessage.getNotification().getTitle());
        customNotification.setData(remoteMessage.getData());
        customNotification.setSendingDate(remoteMessage.getSentTime());
        customNotification.setType(remoteMessage.getNotification().getClickAction());
        customNotification.setMessage(remoteMessage.getNotification().getBody());


        new Thread(new Runnable() {
            @Override
            public void run() {
                saveDataToPreferences(customNotification);

            }
        }).start();

        Map<String, String> data = remoteMessage.getData();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        builder.setContentTitle(remoteMessage.getNotification().getTitle());
        builder.setContentText(remoteMessage.getNotification().getBody());
        if (data.containsKey("offer")) {
            Intent resultIntent = new Intent(this, DetailActivity.class);
            resultIntent.putExtra("offer", data.get("offer"));
            resultIntent.putExtra("details", Constants.DETAILS_OFFLINE_SHOPS);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.setContentIntent(resultPendingIntent);
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());


    }

    private void saveDataToPreferences(CustomNotification newNotif) {
        Context context = this;
        ArrayList<CustomNotification> customNotifList;

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("REFAATPREFS2", MODE_PRIVATE);
        String notifJson = prefs.getString("REFAATPREFS2", "not found");

        if (notifJson.equals("not found")) {
            customNotifList = new ArrayList<>();
            customNotifList.add(newNotif);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("REFAATPREFS2", new Gson().toJson(customNotifList));
            editor.commit();

        } else {

            Type typee = new TypeToken<ArrayList<CustomNotification>>() {
            }.getType();
            try {
                customNotifList = new Gson().fromJson(notifJson, typee);
                customNotifList.add(newNotif);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("REFAATPREFS2", new Gson().toJson(customNotifList));
                editor.commit();

            } catch (Exception e) {
            }


        }

    }
}
