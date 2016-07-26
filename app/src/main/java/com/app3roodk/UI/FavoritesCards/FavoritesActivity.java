package com.app3roodk.UI.FavoritesCards;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app3roodk.Constants;
import com.app3roodk.R;
import com.app3roodk.Schema.Shop;
import com.app3roodk.Schema.User;
import com.app3roodk.UI.About.AboutActivity;
import com.app3roodk.UI.CategoriesActivity.CategoriesActivity;
import com.app3roodk.UI.Feedback.FeedbackActivity;
import com.app3roodk.UI.Offer.AddNewOfferActivity;
import com.app3roodk.UI.Shop.ListShopsActivity;
import com.app3roodk.UI.Shop.ShopActivity;
import com.app3roodk.UtilityFirebase;
import com.app3roodk.UtilityGeneral;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        initNavigationDrawer();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.favorites_container, new FavoritesFragment())
                    .commit();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_SIGN_IN) signingResult(resultCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_menu) {
            mDrawerLayout.openDrawer(GravityCompat.END);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END))
            mDrawerLayout.closeDrawer(GravityCompat.END);
        hideDrawerItems();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END))
            mDrawerLayout.closeDrawer(GravityCompat.END);
        else
            super.onBackPressed();

    }

    //region Navigation Drawer
    public void initNavigationDrawer() {
        // Create Navigation drawer and inlfate layout
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        // Set behavior of Navigation drawer
        hideDrawerItems();
        assert mNavigationView != null;
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        menuItem.setChecked(true);

                        switch (menuItem.getItemId()) {

                            case R.id.action_add_offers:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                if (UtilityGeneral.getNumberOfAvailableOffers(getBaseContext(), UtilityGeneral.getCurrentYearAndWeek()) <= 0) {
                                    Snackbar.make(findViewById(android.R.id.content), "عفواً لايمكنك عمل أكثر من " + String.valueOf(Constants.NUMBER_OF_OFFERS_PER_WEEK) + " عروض فى الإسبوع", Snackbar.LENGTH_LONG).show();
                                    hideDrawerItems();
                                } else
                                    startActivityForResult(new Intent(FavoritesActivity.this, AddNewOfferActivity.class), 222);
                                return true;

                            case R.id.action_signin:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                signingClick();
                                return true;

                            case R.id.action_new_shop:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivityForResult(new Intent(FavoritesActivity.this, ShopActivity.class), 222);
                                return true;

                            case R.id.action_home:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivityForResult(new Intent(FavoritesActivity.this, CategoriesActivity.class), 222);
                                return true;

                            case R.id.action_view_my_shop:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivityForResult(new Intent(FavoritesActivity.this, ListShopsActivity.class), 222);
                                return true;

                            case R.id.action_logout:
                                AuthUI.getInstance()
                                        .signOut(FavoritesActivity.this)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                                UtilityGeneral.removeShop(getBaseContext());
                                                UtilityGeneral.removeUser(getBaseContext());
                                                hideDrawerItems();
                                            }
                                        });
                                return true;

                            case R.id.action_facebookPage:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                openFacebook();
                                return true;

                            case R.id.action_favorites:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                return true;

                            case R.id.action_feedback:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivityForResult(new Intent(FavoritesActivity.this, FeedbackActivity.class), 222);
                                return true;

                            case R.id.action_aboutapp:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivityForResult(new Intent(FavoritesActivity.this, AboutActivity.class), 222);
                                return true;

                            default:
                                mDrawerLayout.closeDrawers();
                                return true;
                        }
                    }
                });
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                showUserInfoNavigationDrawer();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void hideDrawerItems() {
        Menu nav_Menu = ((NavigationView) findViewById(R.id.nav_view)).getMenu();
        nav_Menu.findItem(R.id.action_aboutapp).setVisible(false);
        //Visibility of Nav items
        nav_Menu.findItem(R.id.action_favorites).setChecked(true);
        if (UtilityGeneral.isRegisteredUser(getBaseContext())) {
            nav_Menu.findItem(R.id.action_signin).setVisible(false);
            nav_Menu.findItem(R.id.action_logout).setVisible(true);
            if (UtilityGeneral.isShopExist(getBaseContext())) {
                nav_Menu.findItem(R.id.action_new_shop).setVisible(false);
                nav_Menu.findItem(R.id.action_add_offers).setVisible(true);
                nav_Menu.findItem(R.id.action_view_my_shop).setVisible(true);
            } else {
                nav_Menu.findItem(R.id.action_new_shop).setVisible(true);
                nav_Menu.findItem(R.id.action_add_offers).setVisible(false);
                nav_Menu.findItem(R.id.action_view_my_shop).setVisible(false);
            }
        } else {
            nav_Menu.findItem(R.id.action_signin).setVisible(true);
            nav_Menu.findItem(R.id.action_logout).setVisible(false);
            nav_Menu.findItem(R.id.action_new_shop).setVisible(false);
            nav_Menu.findItem(R.id.action_add_offers).setVisible(false);
            nav_Menu.findItem(R.id.action_view_my_shop).setVisible(false);
        }
    }

    private void showUserInfoNavigationDrawer() {
        //show info of the user
        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                if (UtilityGeneral.isRegisteredUser(getBaseContext()) && UtilityGeneral.isShopExist(getBaseContext())) {
                    ((TextView) mNavigationView.findViewById(R.id.txtNavNumOfOfers)).setText("يمكنك عمل " + String.valueOf(UtilityGeneral.getNumberOfAvailableOffers(getBaseContext(), UtilityGeneral.getCurrentYearAndWeek())) + " عروض فى  هذا الإسبوع");
                } else
                    ((TextView) mNavigationView.findViewById(R.id.txtNavNumOfOfers)).setText("");
                ((TextView) mNavigationView.findViewById(R.id.txtNavName)).setText(auth.getCurrentUser().getDisplayName());
                ((TextView) mNavigationView.findViewById(R.id.txtNavEmail)).setText(auth.getCurrentUser().getEmail());
                if (auth.getCurrentUser().getPhotoUrl() != null)
                    Glide.with(this)
                            .load(auth.getCurrentUser().getPhotoUrl())
                            .asBitmap()
                            .into(new BitmapImageViewTarget((ImageView) mNavigationView.findViewById(R.id.imgNavProfile)) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(FavoritesActivity.this.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    ((ImageView) mNavigationView.findViewById(R.id.imgNavProfile)).setImageDrawable(circularBitmapDrawable);
                                }
                            });
                else
                    Glide.with(this)
                            .load(R.drawable.defaultavatar)
                            .asBitmap()
                            .into(new BitmapImageViewTarget((ImageView) mNavigationView.findViewById(R.id.imgNavProfile)) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(FavoritesActivity.this.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    ((ImageView) mNavigationView.findViewById(R.id.imgNavProfile)).setImageDrawable(circularBitmapDrawable);
                                }
                            });
            } else {
                ((TextView) mNavigationView.findViewById(R.id.txtNavNumOfOfers)).setText("");
                ((TextView) mNavigationView.findViewById(R.id.txtNavName)).setText("");
                ((TextView) mNavigationView.findViewById(R.id.txtNavEmail)).setText("");
                Glide.with(this)
                        .load(R.drawable.logo)
                        .asBitmap()
                        .into(new BitmapImageViewTarget((ImageView) mNavigationView.findViewById(R.id.imgNavProfile)) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(FavoritesActivity.this.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                ((ImageView) mNavigationView.findViewById(R.id.imgNavProfile)).setImageDrawable(circularBitmapDrawable);
                            }
                        });
            }
        } catch (Exception ex) {
//            Log.e("NavShowInfo", ex.getMessage());
        }
    }

    public void openFacebook() {
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/583145478505894")), 222);
        } catch (Exception e) {
            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/3roodk")), 222);
        }
    }

    //endregion

    //region Signing
    User signingUser;
    ProgressDialog signingProgress;

    private void signingClick() {
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
            if (signingProgress.isShowing()) signingProgress.dismiss();
            initNavigationDrawer();
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
//                                            Log.w("Test", "getShop:onCancelled", databaseError.toException());
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
//            Log.e("signingResult", ex.getMessage());
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