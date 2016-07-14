package com.app3roodk.UI.IntroActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.app3roodk.Constants;
import com.app3roodk.R;
import com.app3roodk.Schema.User;
import com.app3roodk.UI.CategoriesActivity.CategoriesActivity;
import com.app3roodk.UtilityGeneral;
import com.google.android.gms.maps.model.LatLng;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        configClicks();
    }

    private void configClicks() {

        findViewById(R.id.btnContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getLatLng(Constants.PERMISSION_MAPS_VISITOR)) return;
                try {
                    User user = new User();
                    LatLng latLng = UtilityGeneral.getCurrentLonAndLat(getBaseContext());
                    user.setLat(String.valueOf(latLng.latitude));
                    user.setLon(String.valueOf(latLng.longitude));
                    UtilityGeneral.saveUser(getBaseContext(), user);
                    startActivity(new Intent(IntroActivity.this, CategoriesActivity.class));
                    finish();
                } catch (Exception ex) {
                    Toast.makeText(getBaseContext(), "ممكن تفتح الخرائط على الأقل مرة", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean getLatLng(int PERMISSION_TYPE) {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getBaseContext(), "Open Location First", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        } else {
            try {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_TYPE);
                } else {
                    return true;
                }

            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), "ممكن تفتح الخرائط على الأقل مرة", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_MAPS_VISITOR: {
                User user = new User();
                LatLng latLng = UtilityGeneral.getCurrentLonAndLat(getBaseContext());
                user.setLat(String.valueOf(latLng.latitude));
                user.setLon(String.valueOf(latLng.longitude));
                UtilityGeneral.saveUser(getBaseContext(), user);
                startActivity(new Intent(this, CategoriesActivity.class));
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UtilityGeneral.isLocationExist(getBaseContext())) {
            startActivity(new Intent(IntroActivity.this, CategoriesActivity.class));
            finish();
        }
    }
}
