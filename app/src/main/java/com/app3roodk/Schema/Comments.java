package com.app3roodk.Schema;

public class Comments {

    private String objectId;
    private String time;
    private String commentText;
    private String userName;
    private String userId;
    private int like;
    private int dislike;


    public Comments() {
    }

    public Comments(String time, String commentText, String userName, String userId, int like, int dislike) {
        this.time = time;
        this.commentText = commentText;
        this.userName = userName;
        this.userId = userId;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
