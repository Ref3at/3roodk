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
    private int like;
    private int dislike;


    public Comments() {
    }

    public Comments(String objectId, String time, String commentText, String userId, String offerId,int like, int dislike) {
        this.objectId = objectId;
        this.time = time;
        this.commentText = commentText;
        this.userId = userId;
        this.offerId = offerId;
        this.like = like;
        this.dislike = dislike;
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

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }
}
