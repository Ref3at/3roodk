package com.app3roodk.Schema;

import java.util.ArrayList;

/**
 * Created by ZooM- on 4/24/2016.
 */
public class Offer {
    private String objectId = "";
    private String title = ""; //*
    private String updatedAt = "";
    private String createdAt = "";
    private String Desc = ""; //*
    private String PriceBefore = ""; //*
    private String PriceAfter = ""; //*
    private String Period = ""; //*
    private String endTime = "";
    private int viewNum = 0;
    private int favoriteNum = 0;
    private String CategoryId = ""; //*
    private String ShopId = ""; //*
    private String ShopName = ""; //*

    // ref3t add
    private ArrayList<String> ImagePaths = new ArrayList<>(); // later will be <img> //*

    // rate
    private int numberUsersRated = 0;
    private int totalRate = 0;
    private int averageRate = 0;

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

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        this.CategoryId = categoryId;
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

    public void setImagePaths(String imagePaths) {
        try {
            this.ImagePaths = new ArrayList<>();
            imagePaths = imagePaths.substring(1, imagePaths.length() - 1);
            String[] images = imagePaths.split(",");
            for (int i = 0; i < images.length; i++) {
                ImagePaths.add(images[i]);
            }
        } catch (Exception ex) {
        }
    }

    public void setImagePaths(ArrayList<String> imagePaths) {
        this.ImagePaths = imagePaths;
    }

    public void addImagePath(String imagePath) {
        if (ImagePaths == null)
            ImagePaths = new ArrayList<>();
        ImagePaths.add(imagePath);
    }

    public void addImagePaths(ArrayList<String> imagePaths) {
        if (imagePaths == null)
            imagePaths = new ArrayList<>();
        imagePaths.addAll(imagePaths);
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
}
