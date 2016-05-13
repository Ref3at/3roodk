package com.app3roodk.UI.PositioningActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app3roodk.R;
import com.app3roodk.Schema.User;
import com.app3roodk.UI.CategoriesActivity.CategoriesActivity;
import com.app3roodk.UI.Signing.SignInActivity;
import com.app3roodk.UI.Signing.SignUpActivity;
import com.app3roodk.UtilityGeneral;
import com.google.android.gms.maps.model.LatLng;

public class PositioningActivity extends AppCompatActivity {

    Button btnSignIn, btnSignUp, btnChooseLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positioning);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnChooseLocation = (Button) findViewById(R.id.btnPosition);
        configClicks();
    }

    private void configClicks() {

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), SignUpActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), SignInActivity.class));
            }
        });

        btnChooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(getBaseContext(), "Open Location First", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                } else {
                    try {
                        User user = new User();
                        LatLng latLng = UtilityGeneral.getCurrentLonAndLat(getBaseContext());
                        user.setLat(String.valueOf(latLng.latitude));
                        user.setLon(String.valueOf(latLng.longitude));
                        UtilityGeneral.saveUser(getBaseContext(), user);
                        startActivity(new Intent(PositioningActivity.this, CategoriesActivity.class));
                        finish();
                    } catch (Exception ex) {
                        Toast.makeText(getBaseContext(), "Make sure that Location Permission is allowed on your device!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UtilityGeneral.isLocationExist(getBaseContext())) {
            startActivity(new Intent(PositioningActivity.this, CategoriesActivity.class));
            finish();
        }
    }
}
