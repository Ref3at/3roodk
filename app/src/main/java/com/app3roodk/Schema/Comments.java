package com.app3roodk.Schema;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Comments {

    private String objectId;
    private String commentText;
    private String userName;
    private String userId;
    private String userNotificationToken;
    private String offerId;
    private HashMap<String,String> userLike ;
    private HashMap<String,String> userDislike ;

    @Exclude
    private ArrayList<Replies> lstReplies;

    private Long creationDate;

    public java.util.Map<String, String> getCreationDate() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    public Long getCreationDateLong() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    private String photoUrl;

    public Comments() {
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @Exclude
    public String getReadableTime(){
        try {
            return new SimpleDateFormat("dd-MM-yyyy  HH:mm").format(new Date(creationDate));
        }
        catch (Exception ex){return "";}
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

    public HashMap<String, String> getUserLike() {
        if(userLike == null ) userLike = new HashMap<>();
        return userLike;
    }

    public void setUserLike(HashMap<String, String> userLike) {
        this.userLike = userLike;
    }

    public HashMap<String, String> getUserDislike() {
        if(userDislike == null ) userDislike = new HashMap<>();
        return userDislike;
    }

    public void setUserDislike(HashMap<String, String> userDislike) {
        this.userDislike = userDislike;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    @Exclude
    public ArrayList<Replies> getLstReplies() {
        if(lstReplies == null)
            lstReplies = new ArrayList<>();
        return lstReplies;
    }
    public void setLstReplies(ArrayList<Replies> lstReplies) {
        this.lstReplies = lstReplies;
    }

    public String getUserNotificationToken() {
        return userNotificationToken;
    }

    public void setUserNotificationToken(String userNotificationToken) {
        this.userNotificationToken = userNotificationToken;
    }
}
