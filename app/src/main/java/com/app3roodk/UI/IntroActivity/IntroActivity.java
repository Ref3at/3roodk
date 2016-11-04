package com.app3roodk.UI.IntroActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.app3roodk.Constants;
import com.app3roodk.R;
import com.app3roodk.Schema.User;
import com.app3roodk.UI.MainActivity.MainActivity;
import com.app3roodk.UtilityGeneral;
import com.dd.morphingbutton.MorphingButton;
import com.dd.morphingbutton.impl.IndeterminateProgressButton;
import com.google.android.gms.maps.model.LatLng;
import com.redbooth.WelcomeCoordinatorLayout;

public class IntroActivity extends AppCompatActivity {

    IndeterminateProgressButton btn_cont;
    IndeterminateProgressButton button;
    private int mMorphCounter1 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        try {
            if (!UtilityGeneral.mGoogleApiClient.isConnected() && !UtilityGeneral.mGoogleApiClient.isConnecting())
                UtilityGeneral.mGoogleApiClient.connect();
        } catch (Exception ex) {
        }
        WelcomeCoordinatorLayout coordinatorLayout
                = (WelcomeCoordinatorLayout) findViewById(R.id.coordinator);
        coordinatorLayout.addPage(R.layout.intro_screen_1, R.layout.intro_screen_2,
                R.layout.intro_screen_3, R.layout.intro_screen_4_finding_city, R.layout.welcome_screen_5);

        btn_cont = (IndeterminateProgressButton) coordinatorLayout.findViewById(R.id.btnContinue);


        btn_cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onMorphButton1Clicked(btn_cont);

            }
        });
        morphToSquare(btn_cont, 0);

    }

    private void onMorphButton1Clicked(final IndeterminateProgressButton btnMorph) {
        if (mMorphCounter1 == 0) {
            mMorphCounter1++;
            morphToSquare(btnMorph, 500);
        } else if (mMorphCounter1 == 1) {
            mMorphCounter1 = 0;


            simulateProgress1(btnMorph);


        }
    }

    private void simulateProgress1(@NonNull final IndeterminateProgressButton button) {
        this.button = button;

        int progressColor1 = ContextCompat.getColor(this, R.color.holo_blue_bright);
        int progressColor2 = ContextCompat.getColor(this, R.color.holo_green_light);
        int progressColor3 = ContextCompat.getColor(this, R.color.holo_orange_light);
        int progressColor4 = ContextCompat.getColor(this, R.color.holo_red_light);
        int color = ContextCompat.getColor(this, R.color.mb_gray);
        int progressCornerRadius = (int) getResources().getDimension(R.dimen.mb_corner_radius_4);
        int width = (int) getResources().getDimension(R.dimen.width_200);
        int height = (int) getResources().getDimension(R.dimen.height_8);
        int duration = 500;

        button.blockTouch(); // prevent user from clicking while button is in progress


        find_Location();


        button.morphToProgress(color, progressCornerRadius, width, height, duration, progressColor1, progressColor2,
                progressColor3, progressColor4);


    }


    private void morphToSuccess(final IndeterminateProgressButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(500)
                .cornerRadius((int) this.getResources().getDimension(R.dimen.height_56))
                .width((int) this.getResources().getDimension(R.dimen.height_56))
                .height((int) this.getResources().getDimension(R.dimen.height_56))
                .color(ContextCompat.getColor(this, R.color.primary))
                .colorPressed(ContextCompat.getColor(this, R.color.primary_dark))
                .icon(R.drawable.ic_done);
        btnMorph.morph(circle);
    }


    private void morphToSquare(final IndeterminateProgressButton btnMorph, int duration) {
        MorphingButton.Params square = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(10)
                .width((int) this.getResources().getDimension(R.dimen.width_100))
                .height((int) this.getResources().getDimension(R.dimen.height_56))
                .color(ContextCompat.getColor(this, R.color.colorPrimary))
                .colorPressed(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .text("المتابعه");
        btnMorph.morph(square);
    }


    void find_Location() {
        if (!getLatLng(Constants.PERMISSION_MAPS_VISITOR)) {
            morphToSquare(button, 0);

            return;
        }
        try {
            User user = new User();
            LatLng latLng = UtilityGeneral.getCurrentLonAndLat(getBaseContext());
            user.setLat(String.valueOf(latLng.latitude));
            user.setLon(String.valueOf(latLng.longitude));
            UtilityGeneral.saveUser(getBaseContext(), user);

            morphToSuccess(button);


            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(IntroActivity.this, MainActivity.class));
                    finish();

                }
            }, 700);


        } catch (Exception ex) {

            morphToSquare(button, 0);
            button.unblockTouch();
            mMorphCounter1 = 1;
            Toast.makeText(getBaseContext(), "ممكن تفتح برنامج خرائط جوجل وتعمل تحديد مكانك", Toast.LENGTH_LONG).show();
        }
    }


    private boolean getLatLng(int PERMISSION_TYPE) {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            morphToSquare(btn_cont, 0);
            button.unblockTouch();
            mMorphCounter1 = 1;


            Toast.makeText(getBaseContext(), "يجب تفعيل خاصيه تحديد الموقع أولاً!", Toast.LENGTH_SHORT).show();
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
                morphToSquare(btn_cont, 0);
                mMorphCounter1 = 1;
                button.unblockTouch();
                Toast.makeText(getBaseContext(), "ممكن تفتح برنامج خرائط جوجل وتعمل تحديد مكانك", Toast.LENGTH_LONG).show();
            }
        }
        button.unblockTouch();
        return false;
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_MAPS_VISITOR: {
                try {
                    User user = new User();
                    LatLng latLng = UtilityGeneral.getCurrentLonAndLat(getBaseContext());
                    user.setLat(String.valueOf(latLng.latitude));
                    user.setLon(String.valueOf(latLng.longitude));
                    UtilityGeneral.saveUser(getBaseContext(), user);
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } catch (Exception ex) {
                    btn_cont.unblockTouch();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        morphToSquare(btn_cont, 0);
        mMorphCounter1 = 1;

        if (UtilityGeneral.isLocationExist(getBaseContext())) {
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            finish();
        }
    }
}
