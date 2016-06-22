package com.app3roodk.Schema;

import android.content.ClipData;

import com.app3roodk.ObjectConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

@SimpleSQLTable(table = "test", provider = "TestProvider")
public class Offer {


    @SimpleSQLColumn(value = "col_int", primary = true)
    public int anInt;

    @SimpleSQLColumn("objectId")
    private String objectId;

    @SimpleSQLColumn("title")
    private String title; //*

    @SimpleSQLColumn("updatedAt")
    private String updatedAt;

    @SimpleSQLColumn("createdAt")
    private String createdAt;

    @SimpleSQLColumn("Desc")
    private String Desc; //*

    @SimpleSQLColumn("Period")
    private String Period; //*

    @SimpleSQLColumn("endTime")
    private String endTime;

    @SimpleSQLColumn("viewNum")
    private int viewNum;

    @SimpleSQLColumn("favoriteNum")
    private int favoriteNum;

    @SimpleSQLColumn("CategoryName")
    private String CategoryName; //*

    @SimpleSQLColumn("ShopId")
    private String ShopId; //*

    @SimpleSQLColumn("ShopName")
    private String ShopName; //*

    @SimpleSQLColumn("city")
    private String city; //*

    @SimpleSQLColumn("lat")
    private String lat; //*

    @SimpleSQLColumn("lon")
    private String lon; //*

    @SimpleSQLColumn("userId")
    private String userId;

    @SimpleSQLColumn("userNotificationToken")
    private String userNotificationToken;


    /*
     * as simple content provider does not accept arraylist and hashmap
     * so we use another two String variables to represent them
     *
     * also we use one another string to represent shop info requried by favorites view
    */
    @SimpleSQLColumn("shopInfoForFavoeites")
    String shopInfoForFavoeites;

    @SimpleSQLColumn("hashmapUsersRates")
    private String hashmapUsersRates;


    @SimpleSQLColumn("arrayListItems")
    private String arrayListItems;


    private HashMap<String, String> usersRates;
    // ref3t add
    // private ArrayList<String> ImagePaths = new ArrayList<>(); // later will be <img> //*
    private List<Item> Items = new ArrayList<>(); // later will be <img> //*
    @SimpleSQLColumn("numberUsersRated")
    private int numberUsersRated;
    @SimpleSQLColumn("totalRate")
    private String totalRate = "0";
    @SimpleSQLColumn("averageRate")
    private String averageRate = "0";

    public Offer() {
    }

    public String getShopInfoForFavoeites() {
        return shopInfoForFavoeites;
    }

    public void setShopInfoForFavoeites(String shopInfoForFavoeites) {
        this.shopInfoForFavoeites = shopInfoForFavoeites;
    }


    // rate

    public String getHashmapUsersRates() {
hashmapUsersRates = ObjectConverter.fromHashmapToStringUsersRates(usersRates);
        return hashmapUsersRates;
    }

    public void setHashmapUsersRates(String hashmapUsersRates) {
        this.hashmapUsersRates = hashmapUsersRates;
        usersRates=ObjectConverter.fromStringToHashmapUsersRates(hashmapUsersRates);
    }

    public String getArrayListItems() {
     //  if (Items.size()>0&& arrayListItems.toString().length()==0) {
           arrayListItems = ObjectConverter.fromArraylistToStringItems(Items);
    //   }
           return arrayListItems;
    }

    public void setArrayListItems(String arrayListItems) {
        this.arrayListItems = arrayListItems;
      if (!arrayListItems.equals(null)) {
          Items = ObjectConverter.fromStringToArraylistItems(arrayListItems);
      }
    }

    public List<Item> getItems() {
        return Items;


    }

    public void setItems(ArrayList<Item> items) {
        Items = items;
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

    public String getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(String totalRate) {
        this.totalRate = totalRate;
    }


    public String getAverageRate() {
        return averageRate;
    }

    public void setAverageRate(String averageRate) {
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public HashMap<String, String> getUsersRates() {
        if (this.usersRates == null) this.usersRates = new HashMap<>();
        return usersRates;
    }

    public void setUsersRates(HashMap<String, String> usersRates) {
        if (this.usersRates == null) this.usersRates = new HashMap<>();
        this.usersRates = usersRates;
    }

    public int getAnInt() {
        return anInt;
    }

    public void setAnInt(int anInt) {
        this.anInt = anInt;
    }
}
