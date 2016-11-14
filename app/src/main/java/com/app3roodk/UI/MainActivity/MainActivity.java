package com.app3roodk.UI.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app3roodk.Constants;
import com.app3roodk.R;
import com.app3roodk.UI.CitySelect.CitySelectionActivity;
import com.app3roodk.Utilities.UtilityViews;
import com.app3roodk.UtilityGeneral;
import com.google.firebase.iid.FirebaseInstanceId;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;

import me.grantland.widget.AutofitTextView;

public class MainActivity extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    Boolean exit = false;

    LikeButton btnNotification;
    RelativeLayout titleCityName;

    AutofitTextView cityName;

    String currentCity;
    String currentGov;

    ViewPager mViewPager;

    SmartTabLayout viewPagerTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Adding Toolbar to Main screen
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        UtilityViews.initNavigationDrawer(this,R.id.action_home);
        FirebaseInstanceId.getInstance().getToken();

        cityName = (AutofitTextView) findViewById(R.id.textView_city);

        titleCityName = (RelativeLayout) findViewById(R.id.title_city_name);
        titleCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, CitySelectionActivity.class);
                startActivityForResult(i, 1);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);

        // Set Tabs inside Toolbar
//        tabs = (TabLayout) findViewById(R.id.tabs);
//        tabs.setupWithViewPager(mViewPager);

        currentCity = UtilityGeneral.loadCity(this);
        cityName.setText("عروض " + currentCity.toString());
        currentGov = UtilityGeneral.loadGov(this);

        setupViewPager(currentGov, currentCity);


//        for (int i = 0; i < tabs.getTabCount(); i++) {
//            TabLayout.Tab tab = tabs.getTabAt(i);
//            RelativeLayout relativeLayout = (RelativeLayout)
//                    LayoutInflater.from(this).inflate(R.layout.tab_layout, tabs, false);
//            TextView tabTextView = (TextView) relativeLayout.findViewById(R.id.tab_title);
//            tabTextView.setText(tab.getText());
//            tab.setCustomView(relativeLayout);
//        }
//        tabs.getTabAt(4).select();

        btnNotification = (LikeButton) findViewById(R.id.btn_notification);
        btnNotification.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Toast.makeText(getApplicationContext(), "subscribed in topic", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Toast.makeText(getApplicationContext(), "subscribed in topic", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_SIGN_IN)
            UtilityViews.signingResult(resultCode, this, null);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String city = data.getStringExtra("city");
                cityName.setText("عروض " + city.toString());
                String gov = data.getStringExtra("gov");


                UtilityGeneral.saveCity(this, city);
                UtilityGeneral.saveGov(this, gov);
                setupViewPager(gov, city);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "لم يتم تغيير المدينه", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END))
            mDrawerLayout.closeDrawer(GravityCompat.END);
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

    private void setupViewPager(String gov, String city) {

//        Adapter adapter = new Adapter(getSupportFragmentManager());
//
//        Bundle arguments5 = new Bundle();
//        MapFragment fragment5 = new MapFragment();
//        MapFragment.SelectedCity = city;
//        MapFragment.SelectedGov = gov;
//
//        if (!fragment5.isAdded()) {
//            fragment5.setArguments(arguments5);
//        }
//        adapter.addFragment(fragment5, "الخريطه");
//
//
//        Bundle arguments2 = new Bundle();
//
//        AllHypersFragment fragment2 = new AllHypersFragment();
//        AllHypersFragment.SelectedCity = city;
//        AllHypersFragment.SelectedGov = gov;
//        if (!fragment2.isAdded()) {
//            fragment2.setArguments(arguments2);
//        }
//        adapter.addFragment(fragment2, "هايبرات");
//
//        Bundle arguments4 = new Bundle();
//        AllShopsFragment fragment4 = new AllShopsFragment();
//        AllShopsFragment.SelectedCity = city;
//        AllShopsFragment.SelectedGov = gov;
//
//        if (!fragment4.isAdded()) {
//            fragment4.setArguments(arguments4);
//        }
//        adapter.addFragment(fragment4, "محلات");
//
//        Bundle arguments1 = new Bundle();
//        ShopsCategoriesFragment.SelectedCity = city;
//        ShopsCategoriesFragment.SelectedGov = gov;
//        ShopsCategoriesFragment fragment1 = new ShopsCategoriesFragment();
//        if (!fragment1.isAdded()) {
//            fragment1.setArguments(arguments1);
//        }
//        adapter.addFragment(fragment1, "الأقسام");
//
//
//        Bundle arguments3 = new Bundle();
//        AllOffersFragment fragment3 = new AllOffersFragment();
//        AllOffersFragment.SelectedCity = city;
//        AllOffersFragment.SelectedGov = gov;
//        if (!fragment3.isAdded()) {
//            fragment3.setArguments(arguments3);
//        }
//        adapter.addFragment(fragment3, "كل العروض");
//
//        mViewPager.setAdapter(adapter);
//        for (int i = 0; i < tabs.getTabCount(); i++) {
//            TabLayout.Tab tab = tabs.getTabAt(i);
//            RelativeLayout relativeLayout = (RelativeLayout)
//                    LayoutInflater.from(this).inflate(R.layout.tab_layout, tabs, false);
//            TextView tabTextView = (TextView) relativeLayout.findViewById(R.id.tab_title);
//            tabTextView.setText(tab.getText());
//            tab.setCustomView(relativeLayout);
//        }
//        tabs.getTabAt(4).select();
//

        AllOffersFragment.SelectedGov = gov;
        AllOffersFragment.SelectedCity = city;

        ShopsCategoriesFragment.SelectedGov = gov;
        ShopsCategoriesFragment.SelectedCity = city;

        AllShopsFragment.SelectedGov = gov;
        AllShopsFragment.SelectedCity = city;

        AllHypersFragment.SelectedGov = gov;
        AllHypersFragment.SelectedCity = city;

        MapFragment.SelectedGov = gov;
        MapFragment.SelectedCity = city;


        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("الخريطه", AllHypersFragment.class)
                .add("الهيبرات", AllHypersFragment.class)
                .add("المحلات", AllShopsFragment.class)
                .add("الاقسام", ShopsCategoriesFragment.class)
                .add("كل العروض", AllOffersFragment.class)

                .create());

        mViewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(mViewPager);
        viewPagerTab.getTabAt(3).performClick();


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