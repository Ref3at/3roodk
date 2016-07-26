package com.app3roodk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;

import com.app3roodk.Schema.Comments;
import com.app3roodk.Schema.Offer;
import com.app3roodk.Schema.Shop;
import com.app3roodk.Schema.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressType;
import com.google.maps.model.GeocodingResult;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UtilityGeneral {

    static public String City;

    static public LatLng getCurrentLonAndLat(Context context) {

        try {
            Location location = getLastKnownLocation(context);
            return new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            try {
                return new LatLng(Double.parseDouble(loadUser(context).getLat()), Double.parseDouble(loadUser(context).getLon()));
            } catch (Exception exx) {
                return null;
            }
        }
    }

    static public String getCurrentCityArabic(Context context) {
        String city;
        try {
            LatLng latlng = getCurrentLonAndLat(context);
            com.google.maps.model.LatLng latlng2 = new com.google.maps.model.LatLng(latlng.latitude, latlng.longitude);
            GeoApiContext context2 = new GeoApiContext().setApiKey(Constants.API_KEY_GEOCODER);
            GeocodingResult[] results = GeocodingApi.reverseGeocode(context2, latlng2).language("ar").await();
            if (results != null) {
                city = getCity(results);
                if (city == null || city.equals("null")) {
                    city = getGovernate(results);
                }
                City = city;
                saveCity(context, city);
            } else
                city = loadCity(context);
        } catch (Exception e) {
            city = loadCity(context);
        }
        return city;
    }

    static public GeocodingResult[] getCurrentGovAndCityArabic(LatLng latLng) {
        GeocodingResult[] results = null;
        try {
            com.google.maps.model.LatLng latlng2 = new com.google.maps.model.LatLng(latLng.latitude, latLng.longitude);
            GeoApiContext context2 = new GeoApiContext().setApiKey(Constants.API_KEY_GEOCODER);
            results = GeocodingApi.reverseGeocode(context2, latlng2).language("ar").await();

        } catch (Exception e) {
//            Log.e("1212", e.getMessage());
        }
        return results;
    }

    static public String getCity(GeocodingResult[] results) {
        String city = null;
        if (results != null) {
            for (GeocodingResult res : results) {
                if (res.types[0] == AddressType.ADMINISTRATIVE_AREA_LEVEL_2) {
                    city = res.formattedAddress;
                    city = city.substring(0, city.indexOf("،"));
                }
            }
            if (city == null)
                for (GeocodingResult res : results) {
                    if (res.types[0] == AddressType.ADMINISTRATIVE_AREA_LEVEL_1) {
                        city = res.formattedAddress;
                        city = city.substring(0, city.indexOf("،"));
                    }
                }
            if (city == null)
                for (GeocodingResult res : results) {
                    if (res.types[0] == AddressType.COUNTRY) {
                        city = res.formattedAddress;
                    }
                }
        }
        return city;
    }

    static public String getGovernate(GeocodingResult[] results) {
        String governate = null;
        if (results != null) {
            for (GeocodingResult res : results) {
                if (res.types[0] == AddressType.ADMINISTRATIVE_AREA_LEVEL_1) {
                    governate = res.formattedAddress;
                    governate = governate.substring(0, governate.indexOf("،"));
                }
            }
            if (governate == null)
                for (GeocodingResult res : results) {
                    if (res.types[0] == AddressType.COUNTRY) {
                        governate = res.formattedAddress;
                    }
                }
        }
        return governate;
    }

    static public Intent DrawPathToCertainShop(Context context, LatLng from, LatLng to) {
        Intent mapsPathsIntent = new Intent(context, MapsPathsActivity.class);
        try {
            mapsPathsIntent.putExtra("fromLat", from.latitude);
            mapsPathsIntent.putExtra("fromLng", from.longitude);
        } catch (Exception ex) {
            mapsPathsIntent.putExtra("fromLat", loadLatLong(context).latitude);
            mapsPathsIntent.putExtra("fromLng", loadLatLong(context).longitude);
        }
        mapsPathsIntent.putExtra("toLat", to.latitude);
        mapsPathsIntent.putExtra("toLng", to.longitude);
        return mapsPathsIntent;
    }

    static public Intent DrawCityShopsOnMap(Context context, ArrayList<Shop> lstShops) {
        Intent mapsShopsIntent = new Intent(context, MapsShopsActivity.class);
        mapsShopsIntent.putExtra("JsonShops", new Gson().toJson(lstShops));
        return mapsShopsIntent;
    }

    static public double calculateDistanceInKM(double latA, double lngA, double latB, double lngB) {
        Location locationA = new Location("point A");

        locationA.setLatitude(latA);
        locationA.setLongitude(lngA);

        Location locationB = new Location("point B");

        locationB.setLatitude(latB);
        locationB.setLongitude(lngB);
        return Math.round((locationA.distanceTo(locationB) / 1000) * 10d) / 10d;
    }

    static public void sortOffersByViews(ArrayList<Offer> lstOffers) {
        Collections.sort(lstOffers, new Comparator<Offer>() {
            @Override
            public int compare(Offer o1, Offer o2) {
                if (o1.getViewNum() >= o2.getViewNum())
                    return -1;
                if (o1.getViewNum() < o2.getViewNum())
                    return 1;
                return 0;
            }
        });
    }

    static public void sortCommentsByTime(ArrayList<Comments> lstComments) {
        Collections.sort(lstComments, new Comparator<Comments>() {
            @Override
            public int compare(Comments o1, Comments o2) {
                if (o1.getCreationDateLong() >= o2.getCreationDateLong())
                    return -1;
                if (o1.getCreationDateLong() < o2.getCreationDateLong())
                    return 1;
                return 0;
            }
        });
    }

    static public void sortOffersByDiscount(ArrayList<Offer> lstOffers) {
        Collections.sort(lstOffers, new Comparator<Offer>() {
            @Override
            public int compare(Offer o1, Offer o2) {
                if ((Double.parseDouble(o1.getItems().get(0).getPriceBefore()) - Double.parseDouble((o1.getItems().get(0).getPriceAfter()))) / Double.parseDouble(o1.getItems().get(0).getPriceBefore()) >= (Double.parseDouble(o2.getItems().get(0).getPriceBefore()) - Double.parseDouble((o2.getItems().get(0).getPriceAfter()))) / Double.parseDouble(o2.getItems().get(0).getPriceBefore()))
                    return -1;
                else
                    return 1;
            }
        });
    }

    static public void sortOffersByNewest(ArrayList<Offer> lstOffers) {
        Collections.sort(lstOffers, new Comparator<Offer>() {
            @Override
            public int compare(Offer o1, Offer o2) {
                if (Double.parseDouble(o1.getCreatedAt()) >= Double.parseDouble(o2.getCreatedAt()))
                    return -1;
                if (Double.parseDouble(o1.getCreatedAt()) < Double.parseDouble(o2.getCreatedAt()))
                    return 1;
                return 0;
            }
        });
    }

    static public boolean isTotalAndAverageRatingChanged(Offer offer) {
        boolean isNew = false;
        try {
            String previousTotal = offer.getTotalRate(), previousAverage = offer.getAverageRate();
            Double average, total = 0.0;
            for (Map.Entry<String, String> entry : offer.getUsersRates().entrySet()) {
                total += Double.parseDouble(entry.getValue());
            }
            average = total / offer.getUsersRates().size();
            if (!String.format("%.0f", total).equals(previousTotal)) {
                offer.setTotalRate(String.format("%.0f", total));
                isNew = true;
            }
            if (!String.format("%.1f", average).equals(previousAverage)) {
                offer.setAverageRate(String.format("%.1f", average));
                isNew = true;
            }
        } catch (Exception ex) {
            isNew = false;
        }
        return isNew;
    }

    static public int getNumberOfAvailableOffers(Context context, String yearWeek) {
        User user = loadUser(context);
        try {
            if (user.getNumOfOffersAvailable().containsKey(yearWeek))
                return user.getNumOfOffersAvailable().get(yearWeek);

            else
                return 4;
        } catch (Exception ex) {
            return 0;
        }
    }

    //region Helping Functions
    static private Location getLastKnownLocation(Context context) {
        LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return bestLocation;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    static public String getCurrentDate(Date date) {
        return new SimpleDateFormat("yyyyMMddHHmm", Locale.US).format(date);
    }

    static public String getCurrentYearAndWeek() {
        Calendar c = Calendar.getInstance();
        return String.valueOf(c.get(Calendar.YEAR)) + String.valueOf(c.get(Calendar.WEEK_OF_YEAR));
    }

    static public String getCategoryEnglishName(String arCategory) {
        String categoryName = "";

        switch (arCategory) {
            case "مطاعم":
                categoryName = "Restaurants";
                break;
            case "سوبر ماركت":
                categoryName = "Super market";

                break;
            case "أدوات منزليه":
                categoryName = "Home Tools";

                break;
            case "موبايل":
                categoryName = "Mobiles";

                break;
            case "كمبيوتر":
                categoryName = "Computers";

                break;
            case "أدوات كهربائيه":
                categoryName = "Electrical Tools";

                break;
            case "إكسسوار":
                categoryName = "Accessories";
                break;
            case "خدمات":
                categoryName = "Services";

                break;
            case "ملابس":
                categoryName = "Clothes";

                break;
            case "أحذيه":
                categoryName = "Shoes";

                break;
        }
        return categoryName;
    }

    static public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    //endregion

    //region Save Load Remove
    static public LatLng loadLatLong(Context context) {
        User user;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userJson = prefs.getString(Constants.KEY_USER, "");
        if (userJson.equals("")) return null;
        else {
            try {
                byte[] data = Base64.decode(userJson, Base64.DEFAULT);
                String text = new String(data, "UTF-8");
                user = new Gson().fromJson(text, User.class);
                return new LatLng(Double.parseDouble(user.getLat()), Double.parseDouble(user.getLon()));
            } catch (Exception e) {
                return null;
            }
        }
    }

    static public void saveOffers(Context context, String key, ArrayList<Offer> lstOffers) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPref.edit();
            String offersJson = new Gson().toJson(lstOffers);
            editor.putString("key" + key, offersJson);
            editor.commit();
        } catch (Exception ex) {
        }
    }

    static public ArrayList<Offer> loadOffers(Context context, String key) {
        ArrayList<Offer> lstOffers = null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String OffersJson = prefs.getString("key" + key, "");
        if (OffersJson.equals("")) return new ArrayList<>();
        else {
            try {
                lstOffers = new Gson().fromJson(OffersJson, new TypeToken<ArrayList<Offer>>() {
                }.getType());
                return lstOffers;
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }
    }

    static public void saveUser(Context context, User user) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPref.edit();
            String userJson = new Gson().toJson(user);
            byte[] data = userJson.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            editor.putString(Constants.KEY_USER, base64);
            editor.commit();
        } catch (UnsupportedEncodingException e) {
        }
    }

    static public User loadUser(Context context) {
        User user = null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userJson = prefs.getString(Constants.KEY_USER, "");
        if (userJson.equals("")) return user;
        else {
            try {
                byte[] data = Base64.decode(userJson, Base64.DEFAULT);
                String text = new String(data, "UTF-8");
                user = new Gson().fromJson(text, User.class);
                return user;
            } catch (Exception e) {
                return user;
            }
        }
    }

    static public void saveCity(Context context, String city) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constants.KEY_CITY, city);
            editor.commit();
        } catch (Exception e) {
        }
    }

    static public String loadCity(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String city = prefs.getString(Constants.KEY_CITY, "No");
        return city;
    }

    static public void saveCities(Context context, String jsonCities) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPref.edit();
            HashMap<String, Boolean> mapCities = new Gson().fromJson(jsonCities, new TypeToken<HashMap<String, Boolean>>() {
            }.getType());
            if (mapCities.size() == 0) return;
            ArrayList<String> lstCities = new ArrayList<>();
            for (Map.Entry<String, Boolean> entry : mapCities.entrySet())
                lstCities.add(entry.getKey());
            editor.putString(Constants.KEY_CITIES, new Gson().toJson(lstCities));
            editor.commit();
        } catch (Exception e) {
        }
    }

    static public ArrayList<String> loadCities(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonCities = prefs.getString(Constants.KEY_CITIES, "No");
        ArrayList<String> lstCities = new ArrayList<>();
        lstCities.add(Constants.YOUR_CITY);
        if (jsonCities.equals("No")) return lstCities;
        lstCities.clear();
        lstCities.addAll((Collection<? extends String>) new Gson().fromJson(jsonCities, new TypeToken<ArrayList<String>>() {
        }.getType()));
        Collections.sort(lstCities);
        lstCities.add(0, Constants.YOUR_CITY);
        return lstCities;
    }

    static public boolean isLocationExist(Context context) {
        try {
            return !loadUser(context).getLat().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    static public boolean isRegisteredUser(Context context) {
        try {
            return !loadUser(context).getObjectId().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    static public void removeUserWithLatLong(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(Constants.KEY_USER);
        editor.commit();
    }

    static public void removeUser(Context context) {
        User temp = loadUser(context);
        User user = new User();
        user.setLat(temp.getLat());
        user.setLon(temp.getLon());
        saveUser(context, user);
    }

    static public void saveShops(Context context, HashMap<String, Shop> mapShops) {
        try {
            if (mapShops == null || mapShops.size() == 0) return;
            HashMap<String, Shop> shops = loadShopsMap(context);
            if (shops == null) {
                shops = mapShops;
            } else {
                for (Map.Entry<String, Shop> entry : mapShops.entrySet()) {
                    if (shops.containsKey(entry.getKey()))
                        shops.remove(entry.getKey());
                    shops.put(entry.getKey(), entry.getValue());
                }
            }
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPref.edit();
            String shopsJson = new Gson().toJson(shops);
            editor.putString(Constants.KEY_SHOP, shopsJson);
            editor.commit();
        } catch (Exception e) {
        }
    }

    static public void saveShops(Context context, ArrayList<Shop> lstShops) {
        try {
            if (lstShops == null || lstShops.size() == 0) return;
            HashMap<String, Shop> shops = loadShopsMap(context);
            if (shops == null) {
                shops = new HashMap<>();
                for (Shop shop : lstShops)
                    shops.put(shop.getObjectId(), shop);
            } else {
                for (Shop shop : lstShops) {
                    if (shops.containsKey(shop.getObjectId()))
                        shops.remove(shop.getObjectId());
                    shops.put(shop.getObjectId(), shop);
                }
            }
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPref.edit();
            String shopsJson = new Gson().toJson(shops);
            editor.putString(Constants.KEY_SHOP, shopsJson);
            editor.commit();
        } catch (Exception e) {
        }
    }

    static public void saveShop(Context context, Shop shop) {
        try {
            HashMap<String, Shop> shops = loadShopsMap(context);
            if (shops == null) {
                shops = new HashMap<>();
            } else {
                if (shops.containsKey(shop.getObjectId()))
                    shops.remove(shop.getObjectId());
            }
            shops.put(shop.getObjectId(), shop);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPref.edit();
            String shopsJson = new Gson().toJson(shops);
            editor.putString(Constants.KEY_SHOP, shopsJson);
            editor.commit();
        } catch (Exception e) {
        }
    }

    static public Shop loadShop(Context context, String shopId) {
        HashMap<String, Shop> shops = loadShopsMap(context);
        if (shops == null) {
            return null;
        } else {
            if (shops.containsKey(shopId))
                return shops.get(shopId);
            else
                return null;
        }
    }

    static public HashMap<String, Shop> loadShopsMap(Context context) {
        HashMap<String, Shop> shops = null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String shopsJson = prefs.getString(Constants.KEY_SHOP, "");
        if (shopsJson.equals("")) return shops;
        else {
            try {
                shops = new Gson().fromJson(shopsJson, new TypeToken<HashMap<String, Shop>>() {
                }.getType());
                return shops;
            } catch (Exception e) {
                return shops;
            }
        }
    }

    static public ArrayList<Shop> loadShopsList(Context context) {
        ArrayList<Shop> shops = null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String shopsJson = prefs.getString(Constants.KEY_SHOP, "");
        if (shopsJson.equals("")) return shops;
        else {
            try {
                HashMap<String, Shop> mapShops = new Gson().fromJson(shopsJson, new TypeToken<HashMap<String, Shop>>() {
                }.getType());
                shops = new ArrayList<>();
                for (Map.Entry<String, Shop> entry : mapShops.entrySet()) {
                    shops.add((entry.getValue()));
                }
                return shops;
            } catch (Exception e) {
                return shops;
            }
        }
    }

    static public boolean isShopExist(Context context) {
        try {
            return loadShopsMap(context).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    static public void removeShop(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(Constants.KEY_SHOP);
        editor.commit();
    }
    //endregion
}
