package com.app3roodk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.app3roodk.UI.Shop.CreateShopFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsShopLocationActivity extends AppCompatActivity {

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if (map != null) {
            initMAPS();
            Toast.makeText(getBaseContext(), "اضغط ضغطه طويله علشان تختار المكان", Toast.LENGTH_LONG).show();
        }
    }

    private void initMAPS() {
        map.addMarker(new MarkerOptions().position(CreateShopFragment.latLngShop).title("Shop").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_account_balance_black_24dp)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(CreateShopFragment.latLngShop, 13));
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                CreateShopFragment.latLngShop = latLng;
                finish();
            }
        });
    }
}

