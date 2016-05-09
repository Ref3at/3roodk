package com.app3roodk.Schema;


import java.util.HashMap;

/**
 * Created by ZooM- on 4/23/2016.
 */
public class User {
    private String objectId;
    private String name;
    private String email;
    private String password;
    private String createdAt;
    private String profileImg;
    private String lat;
    private String lon;
    private String city;
    private String governate;
    private String gender;
    private String type;
    private String favOfferIds;
    private String favShopIds;
    private String planId;
    private String subsCategoryIds;

    // ref3t add
    private String subsShopsIds;

    //rate
    private HashMap<String,String> commentsRate;
    private HashMap<String,String> offersRate;

    public User() {
    }

    public User(String objectId, String name, String email, String createdAt, String profileImg, String lat, String lon, String city, String governate, String type, String favOfferIds, String favShopIds, String planId, String subsCategoryIds, HashMap<String,String> commentsRate, HashMap<String,String> offersRate) {
        this.objectId = objectId;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.profileImg = profileImg;
        this.lat = lat;
        this.lon = lon;
        this.city = city;
        this.governate = governate;
        this.type = type;
        this.favOfferIds = favOfferIds;
        this.favShopIds = favShopIds;
        this.planId = planId;
        this.subsCategoryIds = subsCategoryIds;
        this.commentsRate =commentsRate;
        this.offersRate = offersRate;
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

    public String getFavOfferIds() {
        return favOfferIds;
    }

    public void setFavOfferIds(String favOfferIds) {
        this.favOfferIds = favOfferIds;
    }

    public String getFavShopIds() {
        return favShopIds;
    }

    public void setFavShopIds(String favShopIds) {
        this.favShopIds = favShopIds;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getSubsCategoryIds() {
        return subsCategoryIds;
    }

    public void setSubsCategoryIds(String subsCategoryIds) {
        this.subsCategoryIds = subsCategoryIds;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getSubsShopsIds() {
        return subsShopsIds;
    }

    public void setSubsShopsIds(String subsShopsIds) {
        this.subsShopsIds = subsShopsIds;
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
        this.commentsRate.put(key,Value);
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
        this.offersRate.put(key,Value);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
