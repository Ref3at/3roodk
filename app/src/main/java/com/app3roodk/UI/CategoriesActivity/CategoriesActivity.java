package com.app3roodk.UI.CategoriesActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app3roodk.Constants;
import com.app3roodk.R;
import com.app3roodk.Schema.ExistOffers;
import com.app3roodk.Schema.Shop;
import com.app3roodk.Schema.User;
import com.app3roodk.UI.About.AboutActivity;
import com.app3roodk.UI.FavoritesCards.FavoritesActivity;
import com.app3roodk.UI.Feedback.FeedbackActivity;
import com.app3roodk.UI.Offer.AddNewOfferActivity;
import com.app3roodk.UI.OffersCards.CardsActivity;
import com.app3roodk.UI.Shop.ListShopsActivity;
import com.app3roodk.UI.Shop.ShopActivity;
import com.app3roodk.UtilityFirebase;
import com.app3roodk.UtilityGeneral;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.auth.AuthUI;
import com.github.ybq.android.spinkit.SpinKitView;
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
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class CategoriesActivity extends AppCompatActivity {

    TextView t1, t2, t3, t4, t5, t6, t7, t8, t9; // for text offers no
    View v1, v2, v3, v4, v5, v6, v7, v8, v9; // for red circle

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    Boolean exit = false, bOfferExists = false;
    Spinner spnCities;
    ArrayList<String> lstCities;
    ArrayAdapter<String> adapter;
    ValueEventListener availableOfferListener;
    ValueEventListener activeOffersListener;
    String previousCity;
    Thread trdCurrentLocationSpinner;
    SpinKitView progress;
    User signingUser;
    ProgressDialog signingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        // Adding Toolbar to Main screen
        FirebaseInstanceId.getInstance().getToken();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        t1 = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);
        t3 = (TextView) findViewById(R.id.t3);
        t4 = (TextView) findViewById(R.id.t4);
        t5 = (TextView) findViewById(R.id.t5);
        t6 = (TextView) findViewById(R.id.t6);
        t7 = (TextView) findViewById(R.id.t7);
        t8 = (TextView) findViewById(R.id.t8);
        t9 = (TextView) findViewById(R.id.t9);

        v1 = findViewById(R.id.v1);
        v2 = findViewById(R.id.v2);
        v3 = findViewById(R.id.v3);
        v4 = findViewById(R.id.v4);
        v5 = findViewById(R.id.v5);
        v6 = findViewById(R.id.v6);
        v7 = findViewById(R.id.v7);
        v8 = findViewById(R.id.v8);
        v9 = findViewById(R.id.v9);

        progress = (SpinKitView) findViewById(R.id.progress);
        spnCities = (Spinner) findViewById(R.id.spnCities);
        updateUserLocation();
        initSpinner();
        loadCity();
        initNavigationDrawer();
        initListener();
        if (UtilityGeneral.isRegisteredUser(getBaseContext())) {
            UtilityFirebase.updateUserNotificationToken(getBaseContext(), UtilityGeneral.loadUser(getBaseContext()).getObjectId(), FirebaseInstanceId.getInstance().getToken());
            UtilityFirebase.getUserShops(getBaseContext(), UtilityGeneral.loadUser(getBaseContext()).getObjectId());
            UtilityFirebase.getUserNoOfAvailableOffers(getBaseContext(), UtilityGeneral.getCurrentYearAndWeek()).addValueEventListener(availableOfferListener);
        }
    }

    private void updateUserLocation() {
        try {
            User user = UtilityGeneral.loadUser(getBaseContext());
            LatLng location = UtilityGeneral.getCurrentLonAndLat(getBaseContext());
            if (location == null) return;
            user.setLat(String.valueOf(location.latitude));
            user.setLon(String.valueOf(location.longitude));
            UtilityGeneral.saveUser(getBaseContext(), user);
        } catch (Exception ex) {
        }
    }

    private void initListener() {
        availableOfferListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) return;
                String yearWeek = UtilityGeneral.getCurrentYearAndWeek();
                User user = UtilityGeneral.loadUser(getBaseContext());
                if (user.getNumOfOffersAvailable().containsKey(yearWeek))
                    user.getNumOfOffersAvailable().remove(yearWeek);
                user.getNumOfOffersAvailable().put(yearWeek, dataSnapshot.getValue(Integer.class));
                if (UtilityGeneral.isRegisteredUser(getBaseContext()))
                    UtilityGeneral.saveUser(getBaseContext(), user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        activeOffersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progress.setVisibility(View.INVISIBLE);
                if (dataSnapshot.getValue() == null) {
                    hideViews();
                    bOfferExists = false;
                    Snackbar.make(findViewById(android.R.id.content), "لا يوجد عروض فى منطقتك الحاليه", Snackbar.LENGTH_LONG).show();
                    return;
                }
                int HomeTools, Accessories, Mobiles, Computers, Shoes, Clothes, SuperMarket, Services, Restaurants;
                HomeTools = Accessories = Mobiles = Computers = Shoes = Clothes = SuperMarket = Services = Restaurants = 0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                        if (Double.parseDouble(dataSnapshot1.getValue(ExistOffers.class).getCreateTime()) <= Double.parseDouble(UtilityGeneral.getCurrentDate(new Date())))
                            switch (dataSnapshot1.getValue(ExistOffers.class).getCategory()) {
                                case "Home Tools":
                                    HomeTools++;
                                    break;
                                case "Accessories":
                                    Accessories++;
                                    break;
                                case "Mobiles":
                                    Mobiles++;
                                    break;
                                case "Computers":
                                    Computers++;
                                    break;
                                case "Shoes":
                                    Shoes++;
                                    break;
                                case "Clothes":
                                    Clothes++;
                                    break;
                                case "Super market":
                                    SuperMarket++;
                                    break;
                                case "Services":
                                    Services++;
                                    break;
                                case "Restaurants":
                                    Restaurants++;
                                    break;
                            }
                    } catch (Exception ex) {
                    }
                }
                if (Restaurants > 0) {
                    t1.setText(String.valueOf(Restaurants));
                    t1.setVisibility(View.VISIBLE);
                    v1.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }
                if (HomeTools > 0) {
                    t2.setText(String.valueOf(HomeTools));
                    t2.setVisibility(View.VISIBLE);
                    v2.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }
                if (Accessories > 0) {
                    t3.setText(String.valueOf(Accessories));
                    t3.setVisibility(View.VISIBLE);
                    v3.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }
                if (Mobiles > 0) {
                    t4.setText(String.valueOf(Mobiles));
                    t4.setVisibility(View.VISIBLE);
                    v4.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }
                if (Computers > 0) {
                    t5.setText(String.valueOf(Computers));
                    t5.setVisibility(View.VISIBLE);
                    v5.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }
                if (Shoes > 0) {
                    t6.setText(String.valueOf(Shoes));
                    t6.setVisibility(View.VISIBLE);
                    v6.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }
                if (Clothes > 0) {
                    t7.setText(String.valueOf(Clothes));
                    t7.setVisibility(View.VISIBLE);
                    v7.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }
                if (SuperMarket > 0) {
                    t8.setText(String.valueOf(SuperMarket));
                    t8.setVisibility(View.VISIBLE);
                    v8.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }
                if (Services > 0) {
                    t9.setText(String.valueOf(Services));
                    t9.setVisibility(View.VISIBLE);
                    v9.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }

                if (!bOfferExists) {
                    Snackbar.make(findViewById(android.R.id.content), "لا يوجد عروض فى منطقتك الحاليه", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_SIGN_IN) signingResult(resultCode);
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
        else {
            if (exit) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, "اضغط مره كمان علشان تقفل",
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

    @Override
    protected void onDestroy() {
        try {
            UtilityFirebase.getUserNoOfAvailableOffers(getBaseContext(), UtilityGeneral.getCurrentYearAndWeek()).removeEventListener(availableOfferListener);
        } catch (Exception ex) {
        }
        try {
            UtilityFirebase.getActiveExistOffers(previousCity).removeEventListener(activeOffersListener);
        } catch (Exception ex) {
        }
        super.onDestroy();
    }

    public void goToCards(View view) {
        int x = view.getId();
        Intent i = new Intent(CategoriesActivity.this, CardsActivity.class);
        i.putExtra("city", lstCities.get(spnCities.getSelectedItemPosition()));
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
        startActivityForResult(i, 222);
    }

    private void initSpinner() {
        lstCities = UtilityGeneral.loadCities(getBaseContext());
        adapter = new ArrayAdapter<>(getBaseContext(), R.layout.spinner_item, lstCities);
        spnCities.setAdapter(adapter);
        spnCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    if (i == 0)
                        if (!((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER))
                            Toast.makeText(getBaseContext(), "افتح الـ Location او سيتم أخذ آخر مكان مسجل", Toast.LENGTH_SHORT).show();
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                    hideViews();
                    adapterView.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
                    progress.setVisibility(View.VISIBLE);
                    if (trdCurrentLocationSpinner != null && trdCurrentLocationSpinner.isAlive())
                        trdCurrentLocationSpinner.interrupt();
                    if (i == 0) {
                        trdCurrentLocationSpinner = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final String city = UtilityGeneral.getCurrentCityArabic(getApplicationContext());
                                CategoriesActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (city == null) {
                                            hideViews();
                                            bOfferExists = false;
                                            Snackbar.make(findViewById(android.R.id.content), "لا يوجد عروض فى منطقتك الحاليه", Snackbar.LENGTH_LONG).show();
                                            return;
                                        }
                                        updateOffersNumber(city);
                                    }
                                });
                            }
                        });
                        trdCurrentLocationSpinner.start();
                    } else {
                        updateOffersNumber(lstCities.get(i));
                    }

                } catch (Exception ex) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void updateOffersNumber(String cityName) {

        try {
            UtilityFirebase.getActiveExistOffers(previousCity).removeEventListener(activeOffersListener);
        } catch (Exception ex) {
        }
        try {
            previousCity = cityName;
            UtilityFirebase.getActiveExistOffers(previousCity).addValueEventListener(activeOffersListener);
        } catch (Exception ex) {
        }
    }

    private void loadCity() {
        UtilityFirebase.getCities(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    UtilityGeneral.saveCities(getBaseContext(), responseString);
                    initSpinner();
                } catch (Exception ex) {
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                UtilityGeneral.getCurrentCityArabic(getBaseContext());
            }
        }).start();
    }

    private void hideViews() {

        v1.setVisibility(View.GONE);
        v2.setVisibility(View.GONE);
        v3.setVisibility(View.GONE);
        v4.setVisibility(View.GONE);
        v5.setVisibility(View.GONE);
        v6.setVisibility(View.GONE);
        v7.setVisibility(View.GONE);
        v8.setVisibility(View.GONE);
        v9.setVisibility(View.GONE);

        t1.setVisibility(View.GONE);
        t2.setVisibility(View.GONE);
        t3.setVisibility(View.GONE);
        t4.setVisibility(View.GONE);
        t5.setVisibility(View.GONE);
        t6.setVisibility(View.GONE);
        t7.setVisibility(View.GONE);
        t8.setVisibility(View.GONE);
        t9.setVisibility(View.GONE);

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
                                    startActivityForResult(new Intent(CategoriesActivity.this, AddNewOfferActivity.class), 222);
                                return true;

                            case R.id.action_signin:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                signingClick();
                                return true;

                            case R.id.action_new_shop:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivityForResult(new Intent(CategoriesActivity.this, ShopActivity.class), 222);
                                return true;

                            case R.id.action_home:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                return true;

                            case R.id.action_view_my_shop:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivityForResult(new Intent(CategoriesActivity.this, ListShopsActivity.class), 222);
                                return true;

                            case R.id.action_logout:
                                AuthUI.getInstance()
                                        .signOut(CategoriesActivity.this)
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
                                startActivityForResult(new Intent(CategoriesActivity.this, FavoritesActivity.class), 222);
                                return true;

                            case R.id.action_feedback:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivityForResult(new Intent(CategoriesActivity.this, FeedbackActivity.class), 222);
                                return true;

                            case R.id.action_aboutapp:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivityForResult(new Intent(CategoriesActivity.this, AboutActivity.class), 222);
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
        nav_Menu.findItem(R.id.action_home).setChecked(true);
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
                                            RoundedBitmapDrawableFactory.create(CategoriesActivity.this.getResources(), resource);
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
                                            RoundedBitmapDrawableFactory.create(CategoriesActivity.this.getResources(), resource);
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
                                        RoundedBitmapDrawableFactory.create(CategoriesActivity.this.getResources(), resource);
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
