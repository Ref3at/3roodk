package com.app3roodk.UI.OffersCards;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app3roodk.Constants;
import com.app3roodk.ObjectConverter;
import com.app3roodk.R;
import com.app3roodk.Schema.User;
import com.app3roodk.UI.About.AboutActivity;
import com.app3roodk.UI.FavoritesCards.FavoritesActivity;
import com.app3roodk.UI.Feedback.FeedbackActivity;
import com.app3roodk.UI.Offer.OfferActivity;
import com.app3roodk.UI.PositioningActivity.PositioningActivity;
import com.app3roodk.UI.Shop.ListShopsActivity;
import com.app3roodk.UI.Shop.ShopActivity;
import com.app3roodk.UtilityGeneral;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CardsActivity extends AppCompatActivity {

    Intent mIntent;
    private DrawerLayout mDrawerLayout;

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
        toolbar.setTitle(mIntent.getExtras().getString("title"));
        setSupportActionBar(toolbar);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
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
                        menuItem.setChecked(true);

                        switch (menuItem.getItemId()) {

                            case R.id.action_add_offers:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                startActivity(new Intent(CardsActivity.this, OfferActivity.class));
                                return true;

                            case R.id.action_signin:
                                mDrawerLayout.closeDrawer(GravityCompat.END);
                                PositioningActivity.stay = true;
                                startActivity(new Intent(CardsActivity.this, PositioningActivity.class));
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
        // Adding Floating Action Button to bottom right of main view
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UtilityGeneral.isRegisteredUser(getBaseContext())) {
                    PositioningActivity.stay = true;
                    startActivity(new Intent(getBaseContext(), PositioningActivity.class));
                    return;
                }
                if (!UtilityGeneral.isShopExist(getBaseContext())) {
                    startActivity(new Intent(getBaseContext(), ShopActivity.class));
                    return;
                }
                startActivity(new Intent(getBaseContext(), OfferActivity.class));
            }
        });
    }

    public void facebookIntent() {

        startActivity(getOpenFacebookIntent(getApplicationContext()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        Bundle arguments1 = new Bundle();
        CardsFragment cardContentFragment1 = new CardsFragment();
        if (!cardContentFragment1.isAdded()) {
            cardContentFragment1.setArguments(arguments1);
            cardContentFragment1.fragmentType = Constants.FRAGMENT_NEWEST;
        }
        adapter.addFragment(cardContentFragment1, "الأحدث");
        Bundle arguments2 = new Bundle();
        CardsFragment cardContentFragment2 = new CardsFragment();
        if (!cardContentFragment2.isAdded()) {
            cardContentFragment2.setArguments(arguments2);
            cardContentFragment2.fragmentType = Constants.FRAGMENT_MOST_VIEWED;

        }
        adapter.addFragment(cardContentFragment2, "الأكثر مشاهده");
        Bundle arguments3 = new Bundle();
        CardsFragment cardContentFragment3 = new CardsFragment();
        if (!cardContentFragment3.isAdded()) {
            cardContentFragment3.setArguments(arguments3);
            cardContentFragment3.fragmentType = Constants.FRAGMENT_BEST_SALE;

        }
        adapter.addFragment(cardContentFragment3, "عروض مميزه");
        viewPager.setAdapter(adapter);
    }

    private void hideDrawerItems() {
        Menu nav_Menu = ((NavigationView) findViewById(R.id.nav_view)).getMenu();

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
            mDrawerLayout.openDrawer(GravityCompat.END);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideDrawerItems();
    }

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
