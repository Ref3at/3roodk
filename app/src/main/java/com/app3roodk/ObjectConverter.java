package com.app3roodk;

import android.util.Log;

import com.app3roodk.Schema.Item;
import com.app3roodk.Schema.Offer;
import com.app3roodk.Schema.Shop;
import com.app3roodk.Schema.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by ZooM- on 5/10/2016.
 */
public class ObjectConverter {
    final static public String TAG = "ObjectConverter";

    static public Offer convertJsonToOffer(JSONObject jsonObject) {
        Offer offer = new Offer();
        Item item = new Item();
        ArrayList<Item> items_list = new ArrayList<>();
        items_list.add(item);
        offer.setItems(items_list);

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
                //      offer.setPriceAfter(jsonObject.getString("PriceAfter"));
                offer.getItems().get(0).setPriceAfter(jsonObject.getString("PriceAfter"));
            if (jsonObject.has("PriceBefore") && !jsonObject.isNull("PriceBefore"))
                //    offer.setPriceBefore(jsonObject.getString("PriceBefore"));
                offer.getItems().get(0).setPriceBefore(jsonObject.getString("PriceBefore"));
//            if (jsonObject.has("averageRate") && !jsonObject.isNull("averageRate"))
//                offer.setAverageRate(Integer.parseInt(jsonObject.getString("averageRate")));
            if (jsonObject.has("CategoryId") && !jsonObject.isNull("CategoryId"))
                offer.setCategoryName(jsonObject.getString("CategoryId"));
            if (jsonObject.has("Period") && !jsonObject.isNull("Period"))
                offer.setPeriod(jsonObject.getString("Period"));
            if (jsonObject.has("endTime") && !jsonObject.isNull("endTime"))
                offer.setEndTime(jsonObject.getString("endTime"));
            if (jsonObject.has("favoriteNum") && !jsonObject.isNull("favoriteNum"))
                offer.setFavoriteNum(jsonObject.getInt("favoriteNum"));
            if (jsonObject.has("viewNum") && !jsonObject.isNull("viewNum"))
                offer.setViewNum(jsonObject.getInt("viewNum"));
//            if (jsonObject.has("totalRate") && !jsonObject.isNull("totalRate"))
//                offer.setTotalRate(jsonObject.getInt("totalRate"));
            if (jsonObject.has("numberUsersRated") && !jsonObject.isNull("numberUsersRated"))
                offer.setNumberUsersRated(jsonObject.getInt("numberUsersRated"));
//            if (jsonObject.has("ImagePaths") && !jsonObject.isNull("ImagePaths"))
//                offer.setImagePaths(jsonObject.getString("ImagePaths"));
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            return offer;
        }
    }

    static public HashMap convertOfferToHashMap(Offer offer) {
        HashMap map = new HashMap<>();
        try {
            map.put("title", offer.getTitle());
            map.put("CategoryId", offer.getCategoryName());
            map.put("Desc", offer.getDesc());
            //map.put("PriceBefore", offer.getPriceBefore());
            // map.put("PriceAfter", offer.getPriceAfter());
            //
            map.put("PriceBefore", offer.getItems().get(0).getPriceBefore());
            map.put("PriceAfter", offer.getItems().get(0).getPriceAfter());
            map.put("Period", offer.getPeriod());
            map.put("ImagePaths", offer.getItems().get(0).getImagePaths().toString());
            map.put("ShopId", offer.getShopId());
            map.put("ShopName", offer.getShopName());
            map.put("endTime", offer.getEndTime());
            map.put("averageRate", offer.getAverageRate());
            map.put("viewNum", offer.getViewNum());
            map.put("totalRate", offer.getTotalRate());
            map.put("favoriteNum", offer.getFavoriteNum());
            map.put("numberUsersRated", offer.getNumberUsersRated());
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            return map;
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
            if (jsonObject.has("workingTime") && !jsonObject.isNull("workingTime"))
                shop.setWorkingTime(jsonObject.getString("workingTime"));
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            return shop;
        }
    }

    static public HashMap convertShopToHashMap(Shop shop) {
        HashMap map = new HashMap<>();
        try {
            map.put("name", shop.getName());
            map.put("lat", shop.getLat());
            map.put("lon", shop.getLon());
            map.put("city", shop.getCity());
            map.put("governate", shop.getGovernate());
            map.put("address", shop.getAddress());
            map.put("favoriteNum", "0");
            map.put("workingTime", shop.getWorkingTime());
            map.put("contacts", shop.getContacts().toString());
            map.put("logoId", shop.getLogoId());
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            return map;
        }
    }

    static public User convertJsonToUser(JSONObject jsonObject) {
        User user = new User();
        try {
            user.setObjectId(jsonObject.getString("objectId"));
            user.setCreatedAt(jsonObject.getString("createdAt"));
            user.setUpdatedAt(jsonObject.getString("updatedAt"));
            if (jsonObject.has("name") && !jsonObject.isNull("name"))
                user.setName(jsonObject.getString("name"));
            if (jsonObject.has("lat") && !jsonObject.isNull("lat"))
                user.setLat(jsonObject.getString("lat"));
            if (jsonObject.has("lon") && !jsonObject.isNull("lon"))
                user.setLon(jsonObject.getString("lon"));
            if (jsonObject.has("city") && !jsonObject.isNull("city"))
                user.setCity(jsonObject.getString("city"));
            if (jsonObject.has("governate") && !jsonObject.isNull("governate"))
                user.setGovernate(jsonObject.getString("governate"));
            if (jsonObject.has("gender") && !jsonObject.isNull("gender"))
                user.setGender(jsonObject.getString("gender"));
            if (jsonObject.has("type") && !jsonObject.isNull("type"))
                user.setType(jsonObject.getString("type"));
            if (jsonObject.has("planId") && !jsonObject.isNull("planId"))
                user.setPlanId(jsonObject.getString("planId"));
//            if (jsonObject.has("commentsRate") && !jsonObject.isNull("commentsRate"))
//                user.setCommentsRate(jsonObject.getString("commentsRate"));
//            if (jsonObject.has("offersRate") && !jsonObject.isNull("offersRate"))
//                user.setOffersRate(jsonObject.getString("offersRate"));
            if (jsonObject.has("profileImg") && !jsonObject.isNull("profileImg"))
                user.setProfileImg(jsonObject.getString("profileImg"));
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            return user;
        }
    }

    static public HashMap convertUserToHashMap(User user) {
        HashMap map = new HashMap<>();
        try {
            map.put("name", user.getName());
            map.put("email", user.getEmail());
            map.put("lat", user.getLat());
            map.put("lon", user.getLon());
            map.put("city", user.getCity());
            map.put("governate", user.getGovernate());
            map.put("type", user.getType());
            map.put("favOfferIds", user.getFavOfferIds());
            map.put("favShopIds", user.getFavShopIds());
            map.put("planId", user.getPlanId());
            map.put("subsCategoryIds", user.getSubsCategoryIds());
            map.put("commentsRate", user.getCommentsRate().toString());
            map.put("offersRate", user.getOffersRate().toString());
            map.put("gender", user.getGender());
            map.put("profileImg", user.getProfileImg());
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            return map;
        }
    }

    public static HashMap<String, String> fromStringToHashmapUsersRates(String stringUsersRates) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(stringUsersRates);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HashMap<String, String> hashmapUsersRates = new HashMap<String, String>();

        Iterator<?> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = null;
            try {
                value = jsonObject.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            hashmapUsersRates.put(key, value);

        }
        return hashmapUsersRates;

    }

    public static String fromHashmapToStringUsersRates(HashMap<String, String> usersRates) {
        String stringUsersRates = new Gson().toJson(usersRates);
        return stringUsersRates;
    }


    public static String fromArraylistToStringItems(ArrayList<Item> items) {
        JSONArray jsonAraay = new JSONArray(items);
        return new Gson().toJson(jsonAraay);
    }

    public static ArrayList<Item> fromStringToArraylistItems(String hashmapUsersRates) {


        try {
            return doJsonForitemArrayList(hashmapUsersRates);
        } finally {
            return new ArrayList<>();
        }
        // return itemArrayList;
    }

    private static ArrayList<Item> doJsonForitemArrayList(String hashmapUsersRates) {
        ArrayList<Item> itemArrayList = new ArrayList<>();
        Item item = new Item();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(hashmapUsersRates);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject new_Item_object = null;
            try {
                new_Item_object = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray array = null;
            try {
                array = new_Item_object.getJSONArray("imagePaths");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayList<String> ImagePaths = new ArrayList<>(); // later will be <img> //*
            for (int j = 0; j < array.length(); j++) {
                String s = null;
                try {
                    s = array.getString(j);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ImagePaths.add(s);
            }

            item.setImagePaths(ImagePaths);
            try {
                item.setPriceAfter(new_Item_object.getString(""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                item.setPriceBefore(new_Item_object.getString(""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            itemArrayList.add(item);
        }

        return itemArrayList;
    }


}
