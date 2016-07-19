package com.app3roodk.Schema;

public class ExistOffers {
    String endTime;
    String category;

    public ExistOffers() {
    }

    public ExistOffers(String endTime, String category) {
        this.endTime = endTime;
        this.category = category;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
