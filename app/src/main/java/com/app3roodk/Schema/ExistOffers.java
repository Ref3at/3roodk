package com.app3roodk.Schema;

public class ExistOffers {
    String endTime;
    String createTime;
    String category;

    public ExistOffers() {
    }

    public ExistOffers(String endTime, String category,String createTime) {
        this.endTime = endTime;
        this.category = category;
        this.createTime =createTime;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
