package com.app3roodk.Schema;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Comments {

    private String objectId;
    private String time;
    private String commentText;
    private String userName;
    private String userId;
    private String offerId;
    private ArrayList<String> userLike ;
    private ArrayList<String> userDislike ;

    public Comments() {
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getReadableTime(){
        try {
            return new SimpleDateFormat("dd-MM-yyyy  HH:mm").format(new SimpleDateFormat("yyyyMMddHHmm").parse(time));
        }
        catch (Exception ex){return "";}
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<String> getUserLike() {
        return userLike;
    }

    public void setUserLike(ArrayList<String> userLike) {
        this.userLike = userLike;
    }

    public ArrayList<String> getUserDislike() {
        return userDislike;
    }

    public void setUserDislike(ArrayList<String> userDislike) {
        this.userDislike = userDislike;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }
}
