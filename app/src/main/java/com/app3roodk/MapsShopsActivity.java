package com.app3roodk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.app3roodk.Schema.Shop;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class MapsShopsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    ArrayList<Shop> lstShops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

    }

    private void initMAPS() {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lstShops.get(0).getLat()), Double.parseDouble(lstShops.get(0).getLon())), 13));
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            map = googleMap;
            lstShops = new Gson().fromJson(getIntent().getStringExtra("JsonShops"), new TypeToken<ArrayList<Shop>>() {
            }.getType());
        } catch (Exception e) {
            lstShops = new ArrayList<Shop>();
        }
        if (map != null) {
            if (lstShops.size() > 0) {
                initMAPS();
                for (Shop shop : lstShops) {
                    map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(shop.getLat()), Double.parseDouble(shop.getLon()))).title(shop.getName()));
                }
            } else
                Toast.makeText(getBaseContext(), "No Shops", Toast.LENGTH_LONG).show();
        }
    }
    //endregion
}

