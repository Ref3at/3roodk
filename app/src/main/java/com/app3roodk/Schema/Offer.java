package com.app3roodk.Schema;

/**
 * Created by ZooM- on 4/24/2016.
 */
public class Offer {
    private String objectId;
    private String title;
    private String updatedAt;
    private String createdAt;
    private String desc;
    private String priceBefore;
    private String priceAfter;
    private String period;
    private int viewNum;
    private int favoriteNum;
    private String categoryId;
    private String shopId;

    // ref3t add
    private String offerRateId;
    private String imgId; // later will be <img>

    // rate
    private int numberUsersRated;
    private int totalRate; // later will be <img>


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
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPriceBefore() {
        return priceBefore;
    }

    public void setPriceBefore(String priceBefore) {
        this.priceBefore = priceBefore;
    }

    public String getPriceAfter() {
        return priceAfter;
    }

    public void setPriceAfter(String priceAfter) {
        this.priceAfter = priceAfter;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getViewNum() { return viewNum;}

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
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getOfferRateId() {
        return offerRateId;
    }

    public void setOfferRateId(String offerRateId) {
        this.offerRateId = offerRateId;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
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
}
