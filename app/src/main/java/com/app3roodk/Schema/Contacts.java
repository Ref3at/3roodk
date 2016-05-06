package com.app3roodk.Schema;

/**
 * Created by Refaat on 4/27/2016.
 */
public class Contacts {

    private String objectId;
    private String contactType; // mobile no. or facebook page or mail or website or what's app
    private String contactValue; // no. or link or mail
    private String shopId;


    public Contacts() {
    }

    public Contacts(String objectId, String contactType, String contactValue, String ahopId) {
        this.objectId = objectId;
        this.contactType = contactType;
        this.contactValue = contactValue;
        this.shopId = ahopId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getContactValue() {
        return contactValue;
    }

    public void setContactValue(String contactValue) {
        this.contactValue = contactValue;
    }

    public String getAhopId() {
        return shopId;
    }

    public void setAhopId(String ahopId) {
        this.shopId = ahopId;
    }
}
