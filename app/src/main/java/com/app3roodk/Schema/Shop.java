package com.app3roodk.Schema;

import java.util.HashMap;

/**
 * Created by ZooM- on 4/24/2016.
 */
public class Shop {
    private String objectId;
    private String updatedAt;
    private String createdAt;
    private String name;
    private String lat;
    private String lon;
    private String city;
    private String governate;
    private String address;
    private String userId;
    private HashMap<String, String> contacts;

    //ref3t add
    private String logoId;
    private String subscribedNum;
    private String workingTime;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLogoId() {
        return logoId;
    }

    public void setLogoId(String logoId) {
        this.logoId = logoId;
    }

    public String getSubscribedNum() {
        return subscribedNum;
    }

    public void setSubscribedNum(String subscribedNum) {
        this.subscribedNum = subscribedNum;
    }

    public String getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(String workingTime) {
        this.workingTime = workingTime;
    }

    public HashMap<String, String> getContacts() {
        if (contacts == null)
            contacts = new HashMap<>();
        return contacts;
    }

    public void setContacts(HashMap<String, String> contacts) {
        this.contacts = contacts;
    }

    public void addContacts(HashMap<String, String> contacts) {
        if (contacts == null)
            contacts = new HashMap<>();
        this.contacts.putAll(contacts);
    }

    public void addContact(String key, String value) {
        if (contacts == null)
            contacts = new HashMap<>();
        this.contacts.put(key, value);
    }
}
