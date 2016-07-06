package com.app3roodk.Schema;

import java.util.ArrayList;

public class Item {

    private String PriceBefore;
    private String PriceAfter;

    private ArrayList<String> ImagePaths = new ArrayList<>(); // later will be <img> //*

    public Item() {
    }

    public String getPriceBefore() {
        return PriceBefore;
    }

    public void setPriceBefore(String priceBefore) {
        this.PriceBefore = priceBefore;
    }

    public String getPriceAfter() {
        return PriceAfter;
    }

    public void setPriceAfter(String priceAfter) {
        this.PriceAfter = priceAfter;
    }

    public ArrayList<String> getImagePaths() {
        if (ImagePaths == null)
            ImagePaths = new ArrayList<>();
        return ImagePaths;
    }

    public void setImagePaths(ArrayList<String> imagePaths) {
        this.ImagePaths = imagePaths;
    }

}
