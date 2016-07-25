package com.app3roodk.UI.OffersCards;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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
import com.app3roodk.UI.FavoritesCards.FavoritesActivity;
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
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CardsActivity extends AppCompatActivity implements CardsFragment.Callback, CardsFragment.CallbackSnackBehavior {

    Intent mIntent;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FloatingActionButton fab;

    public static Intent getOpenFacebookIntent(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/583145478505894"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/3roodk"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        if (this.getIntent() != null) {
            mIntent = this.getIntent();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView) findViewById(R.id.toolbarTitle)).setText(mIntent.getExtras().getString("title"));
        setSupportActionBar(toolbar);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(2).select();
        // Create Navigation drawer and inlfate layout
        initNavigationDrawer();
        // Adding Floating Action Button to bottom right of main view
        fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UtilityGeneral.isRegisteredUser(getBaseContext())) {
                    signInBuilder();
                    return;
                }
                if (!UtilityGeneral.isShopExist(getBaseContext())) {
                    addShopInBuilder();
                    return;
                }
                startActivity(new Intent(getBaseContext(), AddNewOfferActivity.class));
            }
        });
        showUserInfoNavigationDrawer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_SIGN_IN) signingResult(resultCode);

    }

    public void initNavigationDrawer() {
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
                        menuItem.setChecked(true);

                        switch (menuItem.getItemId()) {

                            case R.id.action_add_offers:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                if (UtilityGeneral.getNumberOfAvailableOffers(getBaseContext(), UtilityGeneral.getCurrentYearAndWeek()) <= 0) {
                                    Snackbar.make(findViewById(R.id.main_content), "عفواً لايمكنك عمل أكثر من " + String.valueOf(Constants.NUMBER_OF_OFFERS_PER_WEEK) + " عروض فى الإسبوع", Snackbar.LENGTH_LONG).show();
                                    hideDrawerItems();
                                } else
                                    startActivity(new Intent(CardsActivity.this, AddNewOfferActivity.class));
                                return true;

                            case R.id.action_signin:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                signingClick();
                                return true;

                            case R.id.action_new_shop:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivity(new Intent(CardsActivity.this, ShopActivity.class));
                                return true;

                            case R.id.action_home:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                finish();
                                return true;

                            case R.id.action_view_my_shop:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivity(new Intent(CardsActivity.this, ListShopsActivity.class));
                                return true;

                            case R.id.action_logout:
                                AuthUI.getInstance()
                                        .signOut(CardsActivity.this)
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
                                facebookIntent();
                                return true;

                            case R.id.action_favorites:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivity(new Intent(CardsActivity.this, FavoritesActivity.class));
                                return true;

                            case R.id.action_feedback:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivity(new Intent(CardsActivity.this, FeedbackActivity.class));
                                return true;

                            case R.id.action_aboutapp:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivity(new Intent(CardsActivity.this, AboutActivity.class));
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

    public void facebookIntent() {

        startActivity(getOpenFacebookIntent(getApplicationContext()));
    }

    private void signInBuilder() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        signingClick();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(CardsActivity.this);
        builder.setMessage("يجب عليك تسجيل الدخول وإمتلاك محل لتتمكن من إضافة عرض")
                .setPositiveButton("تسجيل الدخول", dialogClickListener)
                .setNegativeButton("إلغاء", dialogClickListener).show();
    }

    private void addShopInBuilder() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        startActivity(new Intent(getBaseContext(), ShopActivity.class));
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(CardsActivity.this);
        builder.setMessage("يجب إضافة محل واحد على الأقل لتتمكن من إضافة عرض")
                .setPositiveButton("إضافة محل", dialogClickListener)
                .setNegativeButton("إلغاء", dialogClickListener).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        Bundle arguments3 = new Bundle();
        CardsFragment cardContentFragment3 = new CardsFragment();
        if (!cardContentFragment3.isAdded()) {
            cardContentFragment3.setArguments(arguments3);
            cardContentFragment3.fragmentType = Constants.FRAGMENT_BEST_SALE;

        }
        adapter.addFragment(cardContentFragment3, "عروض مميزه");
        Bundle arguments2 = new Bundle();
        CardsFragment cardContentFragment2 = new CardsFragment();
        if (!cardContentFragment2.isAdded()) {
            cardContentFragment2.setArguments(arguments2);
            cardContentFragment2.fragmentType = Constants.FRAGMENT_MOST_VIEWED;

        }
        adapter.addFragment(cardContentFragment2, "الأكثر مشاهده");
        Bundle arguments1 = new Bundle();
        CardsFragment cardContentFragment1 = new CardsFragment();
        if (!cardContentFragment1.isAdded()) {
            cardContentFragment1.setArguments(arguments1);
            cardContentFragment1.fragmentType = Constants.FRAGMENT_NEWEST;
        }
        adapter.addFragment(cardContentFragment1, "الأحدث");
        viewPager.setAdapter(adapter);
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
                                            RoundedBitmapDrawableFactory.create(CardsActivity.this.getResources(), resource);
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
                                            RoundedBitmapDrawableFactory.create(CardsActivity.this.getResources(), resource);
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
                                        RoundedBitmapDrawableFactory.create(CardsActivity.this.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                ((ImageView) mNavigationView.findViewById(R.id.imgNavProfile)).setImageDrawable(circularBitmapDrawable);
                            }
                        });

            }
        } catch (Exception ex) {
//            Log.e("NavShowInfo", ex.getMessage());
        }
    }

    private void hideDrawerItems() {
        Menu nav_Menu = mNavigationView.getMenu();
        nav_Menu.findItem(R.id.action_aboutapp).setVisible(false);
        //remove selections
        for (int i = 0; i < nav_Menu.size(); i++)
            nav_Menu.getItem(i).setChecked(false);

        //Visibility of Nav items
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

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END))
            mDrawerLayout.closeDrawer(GravityCompat.END);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_menu) {
            showUserInfoNavigationDrawer();
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
                                                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                                                while (iterator.hasNext())
                                                    shops.add(iterator.next().getValue(Shop.class));
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

    @Override
    public void onItemSelected(RecyclerView recyclerView) {
        fab.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onSnackAppear() {
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();

        params.setBehavior(new SnackBarBehavior());
        fab.requestLayout();
    }

    //endregion

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }
}
