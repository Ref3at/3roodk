package com.app3roodk.UI.IntroActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.app3roodk.Constants;
import com.app3roodk.R;
import com.app3roodk.Schema.User;
import com.app3roodk.UI.CitySelect.CitySelectionActivity;
import com.app3roodk.UtilityGeneral;
import com.github.paolorotolo.appintro.ISlidePolicy;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ultra book on 16-Oct-16.
 */

public class IntoScreen4FindingCity extends Fragment implements ISlidePolicy {

    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 453;
    protected static final int REQUEST_CHOSE_CITY = 333;
    static Button choseFromList;
    Button getGPS;

    static void createUser(Context context) {

        try {
            User user = new User();
            LatLng latLng = UtilityGeneral.getCurrentLonAndLat(context);
            user.setLat(String.valueOf(latLng.latitude));
            user.setLon(String.valueOf(latLng.longitude));
            UtilityGeneral.saveUser(context, user);
            NewIntroActivity.viewPager.setCurrentItem(4, true);
        } catch (Exception ex) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.intro_screen_4_finding_city, container, false);

        choseFromList = (Button) rootView.findViewById(R.id.chosebtn);
        choseFromList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CitySelectionActivity.class);
                getActivity().startActivityForResult(i, REQUEST_CHOSE_CITY);

            }
        });

        getGPS = (Button) rootView.findViewById(R.id.btn_gps);
        getGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (UtilityGeneral.isOnline(getActivity())) {
                    startFindCity();
                } else {
                    Toast.makeText(getActivity(), "من فضلك إتصل بالانترنت أولاً!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    private void startFindCity() {
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            displayLocationSettingsRequest(getActivity());
        } else {
            try {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            Constants.PERMISSION_MAPS_VISITOR);
                } else {
                    displayLocationSettingsRequest(getActivity());
                }

            } catch (Exception ex) {
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_MAPS_VISITOR: {

                displayLocationSettingsRequest(getActivity());
            }
        }
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        //    Log.i(TAG, "All location settings are satisfied.");

                        try {
                            String city = UtilityGeneral.getCurrentCityArabic(getActivity());

                            Toast.makeText(getActivity(), city, Toast.LENGTH_SHORT).show();

                            if (!city.equals("No")) {
                                UtilityGeneral.saveCity(getActivity(), city);
                                NewIntroActivity.introScreen5.updatingCity(city);
                                NewIntroActivity.theCity = city;
                                // NewIntroActivity.viewPager.setCurrentItem(4, true);
                                createUser(getActivity());
                            }

                        } catch (Exception e) {
                        }


                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        //  Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            //    Log.i(TAG, "PendingIntent unable to execute request.");
                            Toast.makeText(getActivity(), "PendingIntent unable to execute request.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        Toast.makeText(getActivity(), "Location settings are inadequate, and cannot be fixed here. Dialog not created.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    public boolean isPolicyRespected() {
        return false;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {

        Toast.makeText(getActivity(), "يجب تحديد المدينه أولاً!", Toast.LENGTH_SHORT).show();

    }
}

