package com.app3roodk.UI.CategoriesActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app3roodk.Back4App.UtilityRestApi;
import com.app3roodk.ObjectConverter;
import com.app3roodk.R;
import com.app3roodk.Schema.User;
import com.app3roodk.UI.Offer.OfferActivity;
import com.app3roodk.UI.OffersCards.CardsActivity;
import com.app3roodk.UI.Shop.ShopActivity;
import com.app3roodk.UI.Signing.SignInActivity;
import com.app3roodk.UI.Signing.SignUpActivity;
import com.app3roodk.UtilityGeneral;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class CategoriesActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("الأقسام");
        setSupportActionBar(toolbar);

        // Create Navigation drawer and inlfate layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Set behavior of Navigation drawer
        hideDrawerItems();
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        menuItem.setChecked(true);

                        // TODO: handle navigation
                        //Check to see which item was clicked and perform the appropriate action.
                        switch (menuItem.getItemId()) {

                            case R.id.action_add_offers:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivity(new Intent(CategoriesActivity.this, OfferActivity.class));
                                return true;

                            case R.id.action_signup:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivity(new Intent(CategoriesActivity.this, SignUpActivity.class));
                                return true;

                            case R.id.action_signin:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivity(new Intent(CategoriesActivity.this, SignInActivity.class));
                                return true;

                            case R.id.action_new_shop:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivity(new Intent(CategoriesActivity.this, ShopActivity.class));
                                return true;

                            case R.id.action_home:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                finish();
                                return true;

                            case R.id.action_new_location:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                changeLocation();
                                return true;

                            case R.id.action_logout:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                UtilityGeneral.removeShop(getBaseContext());
                                UtilityGeneral.removeUser(getBaseContext());
                                hideDrawerItems();
                                return true;

                            default:
                                mDrawerLayout.closeDrawers();
                                return true;
                        }
                    }
                });
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

    public void gooo(View view) {
        int x = view.getId();
        Intent i = new Intent(CategoriesActivity.this, CardsActivity.class);
        switch (x) {

            case R.id.imageButton1:

                i.putExtra("title", "مطاعم");
                i.putExtra("name", "Restaurants");
                break;
            case R.id.imageButton2:
                i.putExtra("title", "أدوات منزليه");
                i.putExtra("name", "Home Tools");

                break;
            case R.id.imageButton3:
                i.putExtra("title", "إكسسوار");
                i.putExtra("name", "Accessories");
                break;
            case R.id.imageButton4:
                i.putExtra("title", "موبايل");
                i.putExtra("name", "Mobiles");
                break;
            case R.id.imageButton5:
                i.putExtra("title", "كمبيوتر");
                i.putExtra("name", "Computers");
                break;
            case R.id.imageButton6:
                i.putExtra("title", "أحذية");
                i.putExtra("name", "Shoes");
                break;
            case R.id.imageButton7:
                i.putExtra("title", "ملابس");
                i.putExtra("name", "Clothes");
                break;
            case R.id.imageButton8:
                i.putExtra("name", "Super market");
                i.putExtra("title", "سوبر ماركت");
                break;
            case R.id.imageButton9:
                i.putExtra("name", "Services");
                i.putExtra("title", "خدمات");
                break;
        }
        CategoriesActivity.this.startActivity(i);
    }

    private void hideDrawerItems() {
        Menu nav_Menu = ((NavigationView) findViewById(R.id.nav_view)).getMenu();
        nav_Menu.findItem(R.id.action_home).setVisible(false);
        if (UtilityGeneral.isRegisteredUser(getBaseContext())) {
            nav_Menu.findItem(R.id.action_signin).setVisible(false);
            nav_Menu.findItem(R.id.action_signup).setVisible(false);
            nav_Menu.findItem(R.id.action_logout).setVisible(true);
            if (UtilityGeneral.isShopExist(getBaseContext())) {
                nav_Menu.findItem(R.id.action_new_shop).setVisible(false);
                nav_Menu.findItem(R.id.action_add_offers).setVisible(true);
            } else {
                nav_Menu.findItem(R.id.action_new_shop).setVisible(true);
                nav_Menu.findItem(R.id.action_add_offers).setVisible(false);
            }
        } else {
            nav_Menu.findItem(R.id.action_signin).setVisible(true);
            nav_Menu.findItem(R.id.action_signup).setVisible(true);
            nav_Menu.findItem(R.id.action_logout).setVisible(false);
            nav_Menu.findItem(R.id.action_new_shop).setVisible(false);
            nav_Menu.findItem(R.id.action_add_offers).setVisible(false);
        }
    }

    private void changeLocation() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getBaseContext(), "Open Location First", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        } else {
            try {
                final User user;
                LatLng latLng = UtilityGeneral.getCurrentLonAndLat(getBaseContext());
                if (!UtilityGeneral.isRegisteredUser(getBaseContext())) {
                    user = new User();
                    user.setLat(String.valueOf(latLng.latitude));
                    user.setLon(String.valueOf(latLng.longitude));
                    UtilityGeneral.saveUser(getBaseContext(), user);
                } else {
                    user = UtilityGeneral.loadUser(getBaseContext());
                    user.setLat(String.valueOf(latLng.latitude));
                    user.setLon(String.valueOf(latLng.longitude));
                    UtilityRestApi.updateUser(getBaseContext(), user.getObjectId(), ObjectConverter.convertUserToHashMap(user), new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            UtilityGeneral.saveUser(getBaseContext(), user);
                        }
                    });
                }
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Make sure that Location Permission is allowed on your device!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideDrawerItems();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END))
            mDrawerLayout.closeDrawer(GravityCompat.END);
        else {
            if (exit) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Press Back again to Exit.",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);

            }
        }
    }
}
