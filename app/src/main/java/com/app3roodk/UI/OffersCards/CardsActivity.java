package com.app3roodk.UI.OffersCards;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.app3roodk.Constants;
import com.app3roodk.R;
import com.app3roodk.UI.Offer.AddNewOfferActivity;
import com.app3roodk.UI.Shop.ShopActivity;
import com.app3roodk.Utilities.UtilityViews;
import com.app3roodk.UtilityGeneral;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CardsActivity extends AppCompatActivity implements CardsOfflineFragment.Callback, CardsOfflineFragment.CallbackSnackBehavior, CardsOnlineFragment.Callback, CardsOnlineFragment.CallbackSnackBehavior {

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
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        UtilityViews.initNavigationDrawer(this, -1);

        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(2).select();
        // Create Navigation drawer and inlfate layout
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_SIGN_IN)
            UtilityViews.signingResult(resultCode, this);

    }

    private void signInBuilder() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        UtilityViews.signingClick(CardsActivity.this);
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
        if (!getIntent().getExtras().getBoolean("online")) {
            CardsOfflineFragment cardContentFragment3 = new CardsOfflineFragment();
            if (!cardContentFragment3.isAdded()) {
                cardContentFragment3.setArguments(arguments3);
                cardContentFragment3.fragmentType = Constants.FRAGMENT_BEST_SALE;

            }
            adapter.addFragment(cardContentFragment3, "عروض مميزه");
            Bundle arguments2 = new Bundle();
            CardsOfflineFragment cardContentFragment2 = new CardsOfflineFragment();
            if (!cardContentFragment2.isAdded()) {
                cardContentFragment2.setArguments(arguments2);
                cardContentFragment2.fragmentType = Constants.FRAGMENT_MOST_VIEWED;

            }
            adapter.addFragment(cardContentFragment2, "الأكثر مشاهده");
            Bundle arguments1 = new Bundle();
            CardsOfflineFragment cardContentFragment1 = new CardsOfflineFragment();
            if (!cardContentFragment1.isAdded()) {
                cardContentFragment1.setArguments(arguments1);
                cardContentFragment1.fragmentType = Constants.FRAGMENT_NEWEST;
            }
            adapter.addFragment(cardContentFragment1, "الأحدث");
            viewPager.setAdapter(adapter);
        } else {
            CardsOnlineFragment cardContentFragment3 = new CardsOnlineFragment();
            if (!cardContentFragment3.isAdded()) {
                cardContentFragment3.setArguments(arguments3);
                cardContentFragment3.fragmentType = Constants.FRAGMENT_BEST_SALE;

            }
            adapter.addFragment(cardContentFragment3, "عروض مميزه");
            Bundle arguments2 = new Bundle();
            CardsOnlineFragment cardContentFragment2 = new CardsOnlineFragment();
            if (!cardContentFragment2.isAdded()) {
                cardContentFragment2.setArguments(arguments2);
                cardContentFragment2.fragmentType = Constants.FRAGMENT_MOST_VIEWED;

            }
            adapter.addFragment(cardContentFragment2, "الأكثر مشاهده");
            Bundle arguments1 = new Bundle();
            CardsOnlineFragment cardContentFragment1 = new CardsOnlineFragment();
            if (!cardContentFragment1.isAdded()) {
                cardContentFragment1.setArguments(arguments1);
                cardContentFragment1.fragmentType = Constants.FRAGMENT_NEWEST;
            }
            adapter.addFragment(cardContentFragment1, "الأحدث");
            viewPager.setAdapter(adapter);
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
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END))
            mDrawerLayout.closeDrawer(GravityCompat.END);
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
