package com.app3roodk.Schema;

/**
 * Created by Refaat on 4/27/2016.
 */
public class Rate {

    private String objectId;

    private String usertId;

    private String ratedId; // this will take offerId or CommentId

    private boolean ratedType; // to avoid rating repetition

    private int rateValue;

    public Rate() {
    }

    public Rate(String objectId, String usertId, String ratedId, boolean ratedType, int rateValue) {
        this.objectId = objectId;
        this.usertId = usertId;
        this.ratedId = ratedId;
        this.ratedType = ratedType;
        this.rateValue = rateValue;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getUsertId() {
        return usertId;
    }

    public void setUsertId(String usertId) {
        this.usertId = usertId;
    }

    public String getRatedId() {
        return ratedId;
    }

    public void setRatedId(String ratedId) {
        this.ratedId = ratedId;
    }

    public boolean isRatedType() {
        return ratedType;
    }

    public void setRatedType(boolean ratedType) {
        this.ratedType = ratedType;
    }

    public int getRateValue() {
        return rateValue;
    }

    public void setRateValue(int rateValue) {
        this.rateValue = rateValue;
    }
}
