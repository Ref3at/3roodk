package com.app3roodk.UI.PositioningActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app3roodk.Constants;
import com.app3roodk.R;
import com.app3roodk.Schema.Shop;
import com.app3roodk.Schema.User;
import com.app3roodk.UI.CategoriesActivity.CategoriesActivity;
import com.app3roodk.UtilityGeneral;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Iterator;

public class PositioningActivity extends AppCompatActivity {

    Button btnSignIn, btnChooseLocation;
    TextView txtSigningIn;
    ProgressBar progressBar;
    User user;
    static public boolean stay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positioning);
        user = new User();
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnChooseLocation = (Button) findViewById(R.id.btnPosition);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        txtSigningIn = (TextView) findViewById(R.id.txtSigningIn);
        configClicks();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                setLoadingVisibility(View.VISIBLE);
                FirebaseUser acct = FirebaseAuth.getInstance().getCurrentUser();
                user.setObjectId(acct.getUid());
                user.setEmail(acct.getEmail());
                user.setName(acct.getDisplayName());
                user.setNotificationToken(FirebaseInstanceId.getInstance().getToken());
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Users").child(acct.getUid());
                myRef.setValue(user, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Toast.makeText(getBaseContext(), "حصل مشكله شوف النت ", Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            database.getReference("Shops").child(user.getObjectId()).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            try {
                                                ArrayList<Shop> shops = new ArrayList<Shop>();
                                                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                                                while (iterator.hasNext())
                                                    shops.add(iterator.next().getValue(Shop.class));
                                                UtilityGeneral.saveShops(getBaseContext(), shops);
                                            } catch (Exception ex) {
                                            }
                                            UtilityGeneral.saveUser(getBaseContext(), user);
                                            startActivity(new Intent(PositioningActivity.this, CategoriesActivity.class));
                                            setLoadingVisibility(View.INVISIBLE);
                                            stay = false;
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.w("Test", "getShop:onCancelled", databaseError.toException());
                                        }
                                    });
                        }
                    }
                });
            } else {
                Toast.makeText(getBaseContext(), "فشل فى تسجيل الدخول", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setLoadingVisibility(int visibility) {
        progressBar.setVisibility(visibility);
        txtSigningIn.setVisibility(visibility);
    }

    private void configClicks() {

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getLatLng()) return;
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setProviders(
                                        AuthUI.EMAIL_PROVIDER,
                                        AuthUI.GOOGLE_PROVIDER)
                                //          AuthUI.FACEBOOK_PROVIDER
                                .build(),
                        Constants.RC_SIGN_IN);
            }
        });

        btnChooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stay = false;
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

    private boolean getLatLng() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getBaseContext(), "Open Location First", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        } else {
            try {
                LatLng latLng = UtilityGeneral.getCurrentLonAndLat(getBaseContext());
                user.setLat(String.valueOf(latLng.latitude));
                user.setLon(String.valueOf(latLng.longitude));
                return true;
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Make sure that Location Permission is allowed on your device!", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!stay && UtilityGeneral.isLocationExist(getBaseContext())) {
            startActivity(new Intent(PositioningActivity.this, CategoriesActivity.class));
            finish();
        }
    }
}
