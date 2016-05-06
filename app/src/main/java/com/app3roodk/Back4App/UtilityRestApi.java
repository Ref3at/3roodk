package com.app3roodk.Back4App;

import android.content.Context;

import com.loopj.android.http.TextHttpResponseHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class UtilityRestApi {

    static public void getOffersWithin(Context context, double latitude, double longitude, double distanceInKM, String CategoryName, TextHttpResponseHandler textHttpResponseHandler) {
        double earthRadius = 6378.1;
        double bearing = 1.57;
        double lat1 = Math.toRadians(latitude);
        double lon1 = Math.toRadians(longitude);
        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distanceInKM / earthRadius) +
                Math.cos(lat1) * Math.sin(distanceInKM / earthRadius) * Math.cos(bearing));
        double lon2 = lon1 + Math.atan2(Math.sin(bearing) * Math.sin(distanceInKM / earthRadius) * Math.cos(lat1),
                Math.cos(distanceInKM / earthRadius) - Math.sin(lat1) * Math.sin(lat2));
        double differenceLat = Math.abs(latitude - Math.toDegrees(lat2));
        double differenceLon = Math.abs(longitude - Math.toDegrees(lon2));

        HashMap mapLat = new HashMap();
        mapLat.put("$gte", String.valueOf(latitude - differenceLat));
        mapLat.put("$lte", String.valueOf(latitude + differenceLat));

        HashMap mapLong = new HashMap();
        mapLong.put("$gte", String.valueOf(longitude - differenceLon));
        mapLong.put("$lte", String.valueOf(longitude + differenceLon));

        HashMap mapLatLong = new HashMap();
        mapLatLong.put("lat", mapLat);
        mapLatLong.put("lon", mapLong);

        HashMap mapTime = new HashMap();
        mapTime.put("$gte", getCurrentDate());

        HashMap mapFinal = new HashMap();
        mapFinal.putAll(getNestedQuery("shopId", "Shop", "objectId", mapLatLong));
        mapFinal.put("endTime", mapTime);

        if (CategoryName != null) {
            HashMap mapCategory = new HashMap();
            mapCategory.put("category", CategoryName);
            mapFinal.putAll(getNestedQuery("categoryId", "Category", "objectId", mapCategory));
        }

        CallRestApi.GET(context, "Offer", mapFinal, textHttpResponseHandler);
    }

//Anonymous user session

    // getOffersWithin was written

    static public void getActiveCategoryOffersByGovAndCity(Context context,String categoryName,String gov,String city,TextHttpResponseHandler handler) {
        HashMap mapShopPlace = new HashMap();
        mapShopPlace.put("city", city);
        mapShopPlace.put("governate", gov);

        HashMap mapTime = new HashMap();
        mapTime.put("$gte", getCurrentDate());

        HashMap mapFinal = new HashMap();
        mapFinal.putAll(getNestedQuery("shopId", "Shop", "objectId", mapShopPlace));
        mapFinal.put("endTime", mapTime);

        if (categoryName != null) {
            HashMap mapCategory = new HashMap();
            mapCategory.put("category", categoryName);
            mapFinal.putAll(getNestedQuery("categoryId", "Category", "objectId", mapCategory));
        }

        CallRestApi.GET(context, "Offer", mapFinal, handler);
    }

    static public void getActiveCategoryOffersByLonAndLat(Context context, double latitude, double longitude, double distanceInKM, String categoryName, TextHttpResponseHandler handler) {
        getOffersWithin(context,latitude,longitude,distanceInKM,categoryName,handler);
    }

    static public void getShopsByCityName(Context context, String cityName, TextHttpResponseHandler handler) {
        HashMap map = new HashMap();
        map.put("city", cityName);
        CallRestApi.GET(context, "Shop", map, handler);
    }

    static public void sortActiveCategoryOfferByNewest(Context context,String categoryName,TextHttpResponseHandler handler) {
        HashMap mapTime = new HashMap();
        mapTime.put("$gte", getCurrentDate());

        HashMap mapFinal = new HashMap();
        mapFinal.put("endTime", mapTime);

        if (categoryName != null) {
            HashMap mapCategory = new HashMap();
            mapCategory.put("category", categoryName);
            mapFinal.putAll(getNestedQuery("categoryId", "Category", "objectId", mapCategory));
        }
        CallRestApi.GET(context, "Offer", mapFinal,"-createdAt" ,handler);
    }

    static public void sortActiveCategoryOfferByNearest() {
    }

    static public void sortActiveCategoryOfferByRating() {
    }

    static public void sortActiveCategoryOfferByDiscount() {
    } //later

    static public void sortActiveCategoryOfferByEndingFirst(Context context,String categoryName,TextHttpResponseHandler handler) {
        HashMap mapTime = new HashMap();
        mapTime.put("$gte", getCurrentDate());

        HashMap mapFinal = new HashMap();
        mapFinal.put("endTime", mapTime);

        if (categoryName != null) {
            HashMap mapCategory = new HashMap();
            mapCategory.put("category", categoryName);
            mapFinal.putAll(getNestedQuery("categoryId", "Category", "objectId", mapCategory));
        }
        CallRestApi.GET(context, "Offer", mapFinal,"endTime" ,handler);
    } //later

    static public void raiseOfferViewsByOne(Context context,String offerId,TextHttpResponseHandler handler) {
        CallRestApi.INCREMENT(context,"Offer",offerId,"viewNum",1,handler);
    }

    static public void raiseOfferFavoritesByOne(Context context,String offerId,TextHttpResponseHandler handler) {
        CallRestApi.INCREMENT(context,"Offer",offerId,"favoriteNum",1,handler);
    }

    static public void loadOfferComments(Context context, String offerId, TextHttpResponseHandler handler) {
        HashMap map = new HashMap();
        map.put("offerId", offerId);
        CallRestApi.GET(context, "Comment", map, handler);
    } // when user scrolling

    static public void getThisOfferShopData(Context context,String shopId,TextHttpResponseHandler handler) {
        CallRestApi.GET(context,"Shop",shopId,handler);
    } // when user clicked on shop name

    static public void getActiveOffersByShopName(Context context, String shopName,TextHttpResponseHandler handler) {
        HashMap mapShop = new HashMap();
        mapShop.put("name", shopName);

        HashMap mapTime = new HashMap();
        mapTime.put("$gte", getCurrentDate());

        HashMap mapFinal = new HashMap();
        mapFinal.putAll(getNestedQuery("shopId", "Shop", "objectId", mapShop));
        mapFinal.put("endTime", mapTime);

        CallRestApi.GET(context, "Offer", mapFinal, handler);
    }

    static public void getExpiredOffersByShopName(Context context, String shopName,TextHttpResponseHandler handler) {
        HashMap mapShop = new HashMap();
        mapShop.put("name", shopName);

        HashMap mapTime = new HashMap();
        mapTime.put("$lte", getCurrentDate());

        HashMap mapFinal = new HashMap();
        mapFinal.putAll(getNestedQuery("shopId", "Shop", "objectId", mapShop));
        mapFinal.put("endTime", mapTime);

        CallRestApi.GET(context, "Offer", mapFinal, handler);
    }


    //********************************************************************

    // Regular User

    static public void registerNewUser(Context context,HashMap values,TextHttpResponseHandler handler) {
        CallRestApi.POST(context,"User",values,handler);
    }

    static public void changeUserName(Context context, String userId, String newUserName, TextHttpResponseHandler handler) {
        HashMap map = new HashMap();
        map.put("name",newUserName);
        CallRestApi.PUT(context, "User", map, userId, handler);
    }

    static public void changePassword(Context context, String userId, String newPassword, TextHttpResponseHandler handler) {
        HashMap map = new HashMap();
        map.put("password",newPassword);
        CallRestApi.PUT(context, "User", map, userId, handler);
    }

    static public void changeUserProfileImg(Context context, String userId, String profileImageId, TextHttpResponseHandler handler) {
        HashMap map = new HashMap();
        map.put("profileImageId",profileImageId);
        CallRestApi.PUT(context, "User", map, userId, handler);
    }

    static public void rateOffer() {
    } // int value from 1 to 5 stars *****

    static public void changeRatedOffer() {
    }

    static public void addNewComment(Context context,HashMap map,TextHttpResponseHandler handler) {
        CallRestApi.POST(context,"Comment",map,handler);
    }

    static public void editHisExistingComment(Context context,String commentId ,String newComment,TextHttpResponseHandler handler) {
        HashMap map = new HashMap();
        map.put("comment",newComment);
        CallRestApi.PUT(context, "Comment", map, commentId, handler);
    }

    static public void deleteHisExistingComment(Context context , String commentId, TextHttpResponseHandler handler) {
        CallRestApi.DELETE(context,"Comment",commentId,handler);
    }

    static public void rateExistingComment() {
    } // boolean value (like or dislike)


    //*********************************************************************

    // Merchant User

    static public void createShop(Context context, HashMap values, TextHttpResponseHandler handler) {
        CallRestApi.POST(context, "Shop", values, handler);
    }

    static public void editShopData(Context context, String shopId, HashMap values, TextHttpResponseHandler handler) {
        CallRestApi.PUT(context, "Shop", values, shopId, handler);
    } // such as brand logo & name & location etc

    static public void setShopPaymentPlan() {
    } // later

    static public void addNewOffer(Context context, HashMap values, TextHttpResponseHandler handler) {
        CallRestApi.POST(context, "Offer", values, handler);
    }

    static public void editExistingOffer(Context context, String offerId, HashMap values, TextHttpResponseHandler handler) {
        CallRestApi.PUT(context, "Offer", values, offerId, handler);
    }

    static public void deleteExistingOffer(Context context , String offerId, TextHttpResponseHandler handler) {
        CallRestApi.DELETE(context,"Offer",offerId,handler);
    }

    //region Helping Methods
    static private String getCurrentDate() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    static private HashMap getNestedQuery(String foreignKey, String className, String key, HashMap properties) {
        HashMap mapQuery = new HashMap();
        mapQuery.put("className", className);
        if (properties != null)
            mapQuery.put("where", properties);
        HashMap mapSelect = new HashMap();
        mapSelect.put("query", mapQuery);
        mapSelect.put("key", key);
        HashMap map = new HashMap();
        map.put("$select", mapSelect);
        HashMap mapFinal = new HashMap();
        mapFinal.put(foreignKey, map);
        return mapFinal;
    }
    //endregion
}
