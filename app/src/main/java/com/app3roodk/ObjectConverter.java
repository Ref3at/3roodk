package com.app3roodk;

import android.util.Log;

import com.app3roodk.Schema.Offer;
import com.app3roodk.Schema.Shop;

import org.json.JSONObject;

/**
 * Created by ZooM- on 5/10/2016.
 */
public class ObjectConverter {
    final static public String TAG = "ObjectConverter";

    static public Offer convertJsonToOffer(JSONObject jsonObject) {
        Offer offer = new Offer();
        try {
            offer.setObjectId(jsonObject.getString("objectId"));
            offer.setCreatedAt(jsonObject.getString("createdAt"));
            offer.setUpdatedAt(jsonObject.getString("updatedAt"));
            if (jsonObject.has("title") && !jsonObject.isNull("title"))
                offer.setTitle(jsonObject.getString("title"));
            if (jsonObject.has("Desc") && !jsonObject.isNull("Desc"))
                offer.setDesc(jsonObject.getString("Desc"));
            if (jsonObject.has("ShopName") && !jsonObject.isNull("ShopName"))
                offer.setShopName(jsonObject.getString("ShopName"));
            if (jsonObject.has("ShopId") && !jsonObject.isNull("ShopId"))
                offer.setShopId(jsonObject.getString("ShopId"));
            if (jsonObject.has("PriceAfter") && !jsonObject.isNull("PriceAfter"))
                offer.setPriceAfter(jsonObject.getString("PriceAfter"));
            if (jsonObject.has("PriceBefore") && !jsonObject.isNull("PriceBefore"))
                offer.setPriceBefore(jsonObject.getString("PriceBefore"));
            if (jsonObject.has("averageRate") && !jsonObject.isNull("averageRate"))
                offer.setAverageRate(Integer.parseInt(jsonObject.getString("averageRate")));
            if (jsonObject.has("CategoryId") && !jsonObject.isNull("CategoryId"))
                offer.setCategoryId(jsonObject.getString("CategoryId"));
            if (jsonObject.has("Period") && !jsonObject.isNull("Period"))
                offer.setPeriod(jsonObject.getString("Period"));
            if (jsonObject.has("endTime") && !jsonObject.isNull("endTime"))
                offer.setEndTime(jsonObject.getString("endTime"));
            if (jsonObject.has("favoriteNum") && !jsonObject.isNull("favoriteNum"))
                offer.setFavoriteNum(jsonObject.getInt("favoriteNum"));
            if (jsonObject.has("viewNum") && !jsonObject.isNull("viewNum"))
                offer.setViewNum(jsonObject.getInt("viewNum"));
            if (jsonObject.has("totalRate") && !jsonObject.isNull("totalRate"))
                offer.setTotalRate(jsonObject.getInt("totalRate"));
            if (jsonObject.has("numberUsersRated") && !jsonObject.isNull("numberUsersRated"))
                offer.setNumberUsersRated(jsonObject.getInt("numberUsersRated"));
            if (jsonObject.has("ImagePaths") && !jsonObject.isNull("ImagePaths"))
                offer.setImagePaths(jsonObject.getString("ImagePaths"));
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            return offer;
        }
    }

    static public Shop convertJsonToShop(JSONObject jsonObject) {
        Shop shop = new Shop();
        try {
            shop.setObjectId(jsonObject.getString("objectId"));
            shop.setCreatedAt(jsonObject.getString("createdAt"));
            shop.setUpdatedAt(jsonObject.getString("updatedAt"));
            if (jsonObject.has("name") && !jsonObject.isNull("name"))
                shop.setName(jsonObject.getString("name"));
            if (jsonObject.has("lat") && !jsonObject.isNull("lat"))
                shop.setLat(jsonObject.getString("lat"));
            if (jsonObject.has("lon") && !jsonObject.isNull("lon"))
                shop.setLon(jsonObject.getString("lon"));
            if (jsonObject.has("city") && !jsonObject.isNull("city"))
                shop.setCity(jsonObject.getString("city"));
            if (jsonObject.has("governate") && !jsonObject.isNull("governate"))
                shop.setGovernate(jsonObject.getString("governate"));
            if (jsonObject.has("address") && !jsonObject.isNull("address"))
                shop.setAddress(jsonObject.getString("address"));
            if (jsonObject.has("averageRate") && !jsonObject.isNull("averageRate"))
                shop.setWorkingTime(jsonObject.getString("workingTime"));
            if (jsonObject.has("logoId") && !jsonObject.isNull("logoId"))
                shop.setLogoId(jsonObject.getString("logoId"));
            if (jsonObject.has("userId") && !jsonObject.isNull("userId"))
                shop.setUserId(jsonObject.getString("userId"));
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            return shop;
        }
    }
}
