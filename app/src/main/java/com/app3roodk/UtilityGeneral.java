package com.app3roodk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.app3roodk.Schema.Comments;
import com.app3roodk.Schema.Offer;
import com.app3roodk.Schema.Shop;
import com.app3roodk.Schema.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
            return new LatLng(Double.parseDouble(loadUser(context).getLat()), Double.parseDouble(loadUser(context).getLon()));
        }
    }

    static public List<Address> getCurrentGovAndCity(Context context) {
        List<Address> addresses = null;
        try {
            LatLng latlng = getCurrentLonAndLat(context);
            Geocoder geo = new Geocoder(context, Locale.getDefault());
            addresses = geo.getFromLocation(latlng.latitude, latlng.longitude, 1);

        } catch (Exception e) {
        }
        return addresses;
    }

    static public String getCurrentCity(Context context) {
        String city;
        try {
            LatLng latlng = getCurrentLonAndLat(context);
            Geocoder geo = new Geocoder(context, Locale.getDefault());
            List<Address> addresses  = geo.getFromLocation(latlng.latitude, latlng.longitude, 1);
            if(addresses != null) {
                city = addresses.get(0).getAddressLine(2);
                City =city;
                saveCity(context,city);
            }
            else
                city = loadCity(context);
        } catch (Exception e) {
            city = loadCity(context);
        }
        return city;
    }


    static public List<Address> getCurrentGovAndCity(Context context, LatLng latLng) {
        List<Address> addresses = null;
        try {
            Geocoder geo = new Geocoder(context, Locale.getDefault());
            addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);

        } catch (Exception e) {
        }
        return addresses;
    }

    static public List<Address> getCurrentGovAndCityInEnglish(Context context, LatLng latLng) {
        List<Address> addresses = null;
        try {
            Geocoder geo = new Geocoder(context, Locale.ENGLISH);
            addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);

        } catch (Exception e) {
        }
        return addresses;
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
                if (Double.parseDouble(o1.getTime()) >= Double.parseDouble(o2.getTime()))
                    return -1;
                if (Double.parseDouble(o1.getTime()) < Double.parseDouble(o2.getTime()))
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
        return new SimpleDateFormat("yyyyMMddHHmm").format(date);
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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
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
        return  city;
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
                HashMap<String,Shop> mapShops= new Gson().fromJson(shopsJson, new TypeToken<HashMap<String, Shop>>() {
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
