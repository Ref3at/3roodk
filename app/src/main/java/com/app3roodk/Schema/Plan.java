package com.app3roodk.Schema;

/**
 * Created by Refaat on 4/27/2016.
 */
public class Plan {

    private String objectId;
    private String planType; // A or B or C

    public Plan() {
    }

    public Plan(String objectId, String planType) {
        this.objectId = objectId;
        this.planType = planType;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }
}
