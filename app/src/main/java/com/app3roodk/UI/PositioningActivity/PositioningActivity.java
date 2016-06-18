package com.app3roodk.UI.PositioningActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app3roodk.R;
import com.app3roodk.Schema.Comments;
import com.app3roodk.Schema.Shop;
import com.app3roodk.Schema.User;
import com.app3roodk.UI.CategoriesActivity.CategoriesActivity;
import com.app3roodk.UI.Signing.SignInActivity;
import com.app3roodk.UI.Signing.SignUpActivity;
import com.app3roodk.UtilityGeneral;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PositioningActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    Button btnSignIn, btnSignUp, btnChooseLocation;
    public GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positioning);
        user = new User();
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnChooseLocation = (Button) findViewById(R.id.btnPosition);
        mAuth = FirebaseAuth.getInstance();
        configClicks();
        initGoogleSignIn();
    }

    private void initGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getLatLng())
                    signInGoogle();
            }
        });
    }


    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        } else {
            // Google Sign In failed, update UI appropriately
            // ...
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d("firebaseAuthWithGoogle", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("sdasdasd", "signInWithCredential:onComplete:" + task.isSuccessful());
                        user.setObjectId(task.getResult().getUser().getUid());
                        user.setEmail(acct.getEmail());
                        user.setName(acct.getDisplayName());
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("Users").child(task.getResult().getUser().getUid());
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

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "حدث مشكلة ما", Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
        if (UtilityGeneral.isLocationExist(getBaseContext())) {
            startActivity(new Intent(PositioningActivity.this, CategoriesActivity.class));
            finish();
        }
    }

}
