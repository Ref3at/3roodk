package com.app3roodk.NotificationServices;

import android.content.Context;

import com.app3roodk.Constants;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.nio.charset.Charset;
import java.util.HashMap;

import cz.msebera.android.httpclient.entity.StringEntity;

public class UtilityCloudMessaging {

    static public String COMMENT_TITLE = "3roodk";
    static public String COMMENT_BODY = " commented on your offer";

    static public void sendNotification(Context context, String sendToToken, String title, String body, String tag, HashMap<String, String> data,String clickAction ,TextHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "key="+Constants.FIREBASE_SERVER_KEY_CLOUD_MESSAGING);
        client.addHeader("Content-Type", "application/json");
        StringEntity entity;
        HashMap<String, Object> Properties = new HashMap<>();
        Properties.put("to", sendToToken);
        HashMap<String, Object> NotificationMap = new HashMap<>();
        NotificationMap.put("title", title);
        NotificationMap.put("body", body);
        NotificationMap.put("sound", "default");
        NotificationMap.put("click_action", clickAction);
        NotificationMap.put("tag", tag);
        Properties.put("notification", NotificationMap);
        if(data!=null)
            Properties.put("data",data);
        try {
            entity = new StringEntity(new Gson().toJson(Properties), Charset.defaultCharset());
        } catch (Exception e) {
            return;
        }
        client.post(context, "https://fcm.googleapis.com/fcm/send", entity, "application/json", handler);
    }

}
