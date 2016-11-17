package com.app3roodk.Schema;

import java.util.Map;

/**
 * Created by ultra book on 17-Nov-16.
 */

public class CustomNotification {

    private String type; // comment or replay or promotional
    private String title;
    private long sendingDate;
    private String message;
    private Map<String, String> data;
    private boolean isRead;
    private String imageURL;

    public CustomNotification() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public long getSendingDate() {
        return sendingDate;
    }

    public void setSendingDate(long sendingDate) {
        this.sendingDate = sendingDate;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
