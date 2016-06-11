package com.app3roodk.Schema;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private String objectId;
    private String name;
    private String email;
    private String createdAt;
    private String updatedAt;
    private String profileImg;
    private String lat;
    private String lon;
    private String city;
    private String governate;
    private String gender;
    private String type;
    private ArrayList<String> favOfferIds ;
    private ArrayList<String> favShopIds ;
    private String planId;
    private ArrayList<String> subsCategoryIds;
    private String NotificationToken;

    // ref3t add
    private ArrayList<String> subsShopsIds;

    //rate
    private HashMap<String, String> commentsRate = new HashMap<>();
    private HashMap<String, String> offersRate = new HashMap<>();

    public User() {
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGovernate() {
        return governate;
    }

    public void setGovernate(String governate) {
        this.governate = governate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public HashMap<String, String> getCommentsRate() {
        return commentsRate;
    }

    public void setCommentsRate(HashMap<String, String> commentsRate) {
        this.commentsRate = commentsRate;
    }

    public void addCommentsRate(HashMap<String, String> commentsRate) {
        this.commentsRate.putAll(commentsRate);
    }

    public void addCommentsRate(String key, String Value) {
        this.commentsRate.put(key, Value);
    }

    public HashMap<String, String> getOffersRate() {
        return offersRate;
    }

    public void setOffersRate(HashMap<String, String> offersRate) {
        this.offersRate = offersRate;
    }

    public void addOffersRate(HashMap<String, String> offersRate) {
        this.offersRate.putAll(offersRate);
    }

    public void addOffersRate(String key, String Value) {
        this.offersRate.put(key, Value);
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ArrayList<String> getFavOfferIds() {
        return favOfferIds;
    }

    public void setFavOfferIds(ArrayList<String> favOfferIds) {
        this.favOfferIds = favOfferIds;
    }

    public ArrayList<String> getFavShopIds() {
        return favShopIds;
    }

    public void setFavShopIds(ArrayList<String> favShopIds) {
        this.favShopIds = favShopIds;
    }

    public ArrayList<String> getSubsCategoryIds() {
        return subsCategoryIds;
    }

    public void setSubsCategoryIds(ArrayList<String> subsCategoryIds) {
        this.subsCategoryIds = subsCategoryIds;
    }

    public ArrayList<String> getSubsShopsIds() {
        return subsShopsIds;
    }

    public void setSubsShopsIds(ArrayList<String> subsShopsIds) {
        this.subsShopsIds = subsShopsIds;
    }

    public String getNotificationToken() {
        return NotificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        NotificationToken = notificationToken;
    }
}
