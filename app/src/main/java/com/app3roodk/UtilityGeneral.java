package com.app3roodk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.app3roodk.Schema.Shop;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UtilityGeneral {

    final static private String TAG = "UtilityGeneral";

    static public LatLng getCurrentLonAndLat(Context context) {
        try {
            Location location = getLastKnownLocation(context);
            return new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    static public List<Address> getCurrentGovAndCity(Context context) {
        List<Address> addresses = null;
        try {
            LatLng latlng = getCurrentLonAndLat(context);
            Geocoder geo = new Geocoder(context, Locale.getDefault());
            addresses = geo.getFromLocation(latlng.latitude, latlng.longitude, 1);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return addresses;
    }

    static public Intent DrawPathToCertainShop(Context context, LatLng from, LatLng to) {
        Intent mapsPathsIntent = new Intent(context, MapsPathsActivity.class);
        try {
            mapsPathsIntent.putExtra("fromLat", from.latitude);
            mapsPathsIntent.putExtra("fromLng", from.longitude);
        }
        catch (Exception ex)
        {
            Log.e(TAG,ex.getMessage());
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
    //endregion
}
