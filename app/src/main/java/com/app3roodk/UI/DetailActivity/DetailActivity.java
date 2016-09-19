package com.app3roodk.UI.DetailActivity;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app3roodk.Constants;
import com.app3roodk.R;
import com.app3roodk.Schema.Shop;
import com.app3roodk.Schema.User;
import com.app3roodk.UtilityFirebase;
import com.app3roodk.UtilityGeneral;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    DetailOfflineFragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState == null) {
            if (getIntent().getExtras().getInt("details") == Constants.DETAILS_ONLINE) {
                DetailOnlineFragment fragment = new DetailOnlineFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_content, fragment)
                        .commit();
            } else if (getIntent().getExtras().getInt("details") == Constants.DETAILS_OFFLINE_SHOPS) {
                mFragment = new DetailOfflineFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_content, mFragment)
                        .commit();
            } else {
                DetailOfflineHyperFragment fragment = new DetailOfflineHyperFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_content, fragment)
                        .commit();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_SIGN_IN) signingResult(resultCode);
    }

    //region Signing
    User signingUser;
    ProgressDialog signingProgress;

    public void signingClick() {
        if (!getLatLng(Constants.PERMISSION_MAPS_SIGN_IN)) return;
        signingUser = new User();
        try {
            LatLng latLng = UtilityGeneral.getCurrentLonAndLat(getBaseContext());
            signingUser.setLat(String.valueOf(latLng.latitude));
            signingUser.setLon(String.valueOf(latLng.longitude));
        } catch (Exception ex) {
            Toast.makeText(getBaseContext(), "ممكن تفتح الخرائط على الأقل مرة", Toast.LENGTH_LONG).show();
        }
        startActivityForResult(
                UtilityFirebase.getAuthIntent(),
                Constants.RC_SIGN_IN);
    }

    private void showSigningProgressDialog() {
        signingProgress = ProgressDialog.show(this, "تسجيل الدخول",
                "جاري التحميل...", true);
    }

    private void setLoadingVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            if (!(signingProgress != null && signingProgress.isShowing()))
                showSigningProgressDialog();
        } else if (visibility == View.GONE) {
            if (mFragment != null)
                mFragment.validateUser();
            if (signingProgress.isShowing()) signingProgress.dismiss();

        }
    }

    private void signingResult(int resultCode) {
        try {
            if (resultCode == RESULT_OK) {
                setLoadingVisibility(View.VISIBLE);
                FirebaseUser acct = FirebaseAuth.getInstance().getCurrentUser();
                signingUser.setObjectId(acct.getUid());
                signingUser.setEmail(acct.getEmail());
                signingUser.setName(acct.getDisplayName());
                signingUser.setNotificationToken(FirebaseInstanceId.getInstance().getToken());
                UtilityFirebase.getUser(acct.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User temp = dataSnapshot.getValue(User.class);
                        if (temp == null) {
                            UtilityFirebase.getUser(signingUser.getObjectId()).setValue(signingUser, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Toast.makeText(getBaseContext(), "حصل مشكله شوف النت ", Toast.LENGTH_SHORT).show();
                                    } else {
                                        UtilityGeneral.saveUser(getBaseContext(), signingUser);
                                    }
                                    setLoadingVisibility(View.GONE);
                                }
                            });
                        } else {
                            signingUser = temp;
                            UtilityFirebase.getUserShops(signingUser.getObjectId()).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            try {
                                                ArrayList<Shop> shops = new ArrayList<>();
                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                                    shops.add(dataSnapshot1.getValue(Shop.class));
                                                UtilityGeneral.saveShops(getBaseContext(), shops);
                                            } catch (Exception ex) {
                                            }
                                            UtilityGeneral.saveUser(getBaseContext(), signingUser);
                                            setLoadingVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.w("Test", "getShop:onCancelled", databaseError.toException());
                                            setLoadingVisibility(View.GONE);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(getBaseContext(), "فشل فى تسجيل الدخول", Toast.LENGTH_LONG).show();
                setLoadingVisibility(View.GONE);
            }
        } catch (Exception ex) {
            Log.e("signingResult", ex.getMessage());
        }
    }

    private boolean getLatLng(int PERMISSION_TYPE) {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getBaseContext(), "Open Location First", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 222);
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
            case Constants.PERMISSION_MAPS_SIGN_IN: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LatLng latLng = UtilityGeneral.getCurrentLonAndLat(getBaseContext());
                    signingUser.setLat(String.valueOf(latLng.latitude));
                    signingUser.setLon(String.valueOf(latLng.longitude));
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER,
                                            AuthUI.FACEBOOK_PROVIDER)
                                    .setTheme(R.style.FirbaseUITheme)
                                    .build(),
                            Constants.RC_SIGN_IN);
                }
            }
        }
    }

    //endregion
}
