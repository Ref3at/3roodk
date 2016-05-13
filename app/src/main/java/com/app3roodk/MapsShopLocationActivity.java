package com.app3roodk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app3roodk.UI.Shop.CreateShopFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsShopLocationActivity extends AppCompatActivity {

    private GoogleMap map;
    private Marker marker;
    private Button btnHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        btnHere = (Button) findViewById(R.id.btnHere);
        btnHere.setVisibility(View.VISIBLE);
        if (map != null) {
            initMAPS();
            Toast.makeText(getBaseContext(), "اضغط ضغطه طويله علشان تختار المكان", Toast.LENGTH_LONG).show();
        }
        btnHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initMAPS() {
        marker = map.addMarker(new MarkerOptions().position(CreateShopFragment.latLngShop).title("Shop").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_account_balance_black_24dp)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(CreateShopFragment.latLngShop, 12));
        map.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                marker.remove();
                marker = map.addMarker(new MarkerOptions().position(latLng).title("Shop").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_account_balance_black_24dp)));
                CreateShopFragment.latLngShop = latLng;
            }
        });
    }
}

