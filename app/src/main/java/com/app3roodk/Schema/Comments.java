package com.app3roodk.Schema;

/**
 * Created by Refaat on 4/27/2016.
 */
public class Comments {

    private String objectId;
    private String time;
    private String commentText;
    private String userId;
    private String offerId;


    private String commentRateId;



    public Comments() {
    }

    public Comments(String objectId, String time, String commentText, String userId, String offerId, String commentRate) {
        this.objectId = objectId;
        this.time = time;
        this.commentText = commentText;
        this.userId = userId;
        this.offerId = offerId;
        this.commentRateId = commentRate;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
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

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getCommentRate() {
        return commentRateId;
    }

    public void setCommentRate(String commentRate) {
        this.commentRateId = commentRate;
    }
}
