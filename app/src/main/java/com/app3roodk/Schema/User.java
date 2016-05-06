package com.app3roodk.Schema;


/**
 * Created by ZooM- on 4/23/2016.
 */
public class User {
    private String objectId;
    private String name;
    private String email;
    private String createdAt;
    private String profileImgId;
    private String lat;
    private String lon;
    private String city;
    private String governate;
    private String type;
    private String favOfferIds;
    private String favShopIds;
    private String planId;
    private String subsCategoryIds;

    // ref3t add
    private String subsShopsIds;



    public User() {
    }

    public User(String objectId, String name, String email, String createdAt, String profileImgId, String lat, String lon, String city, String governate, String type, String favOfferIds, String favShopIds, String planId, String subsCategoryIds) {
        this.objectId = objectId;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.profileImgId = profileImgId;
        this.lat = lat;
        this.lon = lon;
        this.city = city;
        this.governate = governate;
        this.type = type;
        this.favOfferIds = favOfferIds;
        this.favShopIds = favShopIds;
        this.planId = planId;
        this.subsCategoryIds = subsCategoryIds;
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

    public String getProfileImgId() {
        return profileImgId;
    }

    public void setProfileImgId(String profileImgId) {
        this.profileImgId = profileImgId;
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
}
