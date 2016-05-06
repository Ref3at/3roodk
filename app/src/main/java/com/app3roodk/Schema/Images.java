package com.app3roodk.Schema;

/**
 * Created by Refaat on 4/27/2016.
 */
public class Images {

    private String objectId;
    private String imgPath;

    public Images() {
    }

    public Images(String objectId, String imgPath) {
        this.objectId = objectId;
        this.imgPath = imgPath;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
