package com.app3roodk.Schema;

import java.util.ArrayList;

public class Offer {
    private String objectId ;
    private String title ; //*
    private String updatedAt;
    private String createdAt ;
    private String Desc; //*
    private String PriceBefore; //*
    private String PriceAfter ; //*
    private String Period ; //*
    private String endTime ;
    private int viewNum;
    private int favoriteNum;
    private String CategoryName; //*
    private String ShopId ; //*
    private String ShopName ; //*
    private String lat ; //*
    private String lon ; //*
    private String userId;
    private String userNotificationToken;

    // ref3t add
    private ArrayList<String> ImagePaths = new ArrayList<>(); // later will be <img> //*

    // rate
    private int numberUsersRated;
    private int totalRate;
    private int averageRate;

    public Offer() {
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        this.Desc = desc;
    }

    public String getPriceBefore() {
        return PriceBefore;
    }

    public void setPriceBefore(String priceBefore) {
        this.PriceBefore = priceBefore;
    }

    public String getPriceAfter() {
        return PriceAfter;
    }

    public void setPriceAfter(String priceAfter) {
        this.PriceAfter = priceAfter;
    }

    public String getPeriod() {
        return Period;
    }

    public void setPeriod(String period) {
        this.Period = period;
    }

    public int getViewNum() {
        return viewNum;
    }

    public void setViewNum(int viewNum) {
        this.viewNum = viewNum;
    }

    public int getFavoriteNum() {
        return favoriteNum;
    }

    public void setFavoriteNum(int favoriteNum) {
        this.favoriteNum = favoriteNum;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        this.CategoryName = categoryName;
    }

    public String getShopId() {
        return ShopId;
    }

    public void setShopId(String shopId) {
        this.ShopId = shopId;
    }

    public int getNumberUsersRated() {
        return numberUsersRated;
    }

    public void setNumberUsersRated(int numberUsersRated) {
        this.numberUsersRated = numberUsersRated;
    }

    public int getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(int totalRate) {
        this.totalRate = totalRate;
    }

    public ArrayList<String> getImagePaths() {
        if (ImagePaths == null)
            ImagePaths = new ArrayList<>();
        return ImagePaths;
    }

    public void setImagePaths(ArrayList<String> imagePaths) {
        this.ImagePaths = imagePaths;
    }

    public int getAverageRate() {
        return averageRate;
    }

    public void setAverageRate(int averageRate) {
        this.averageRate = averageRate;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        this.ShopName = shopName;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNotificationToken() {
        return userNotificationToken;
    }

    public void setUserNotificationToken(String userNotificationToken) {
        this.userNotificationToken = userNotificationToken;
    }
}
