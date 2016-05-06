package com.app3roodk.Schema;

/**
 * Created by Refaat on 4/27/2016.
 */
public class Categories {

    private String objectId;
    private String categoryName; // Clothes or resturants etc. from spinner

    public Categories() {
    }

    public Categories(String objectId, String categoryName) {
        this.objectId = objectId;
        this.categoryName = categoryName;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
