package com.app3roodk.UI.OffersCards;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app3roodk.R;
import com.app3roodk.UI.Offer.OfferActivity;
import com.app3roodk.UI.Shop.ShopActivity;
import com.app3roodk.UI.Signing.SignUpActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Provides UI for the main screen.
 */

/**
 * Provides UI for the main screen.
 */
public class CardsActivity extends AppCompatActivity {

    Intent mIntent;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        if (this.getIntent() != null) {
            mIntent = this.getIntent();
        }


        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setTitle("gggggggggg");
        toolbar.setTitle(mIntent.getExtras().getString("titl"));
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
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Set behavior of Navigation drawer
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
                                startActivity(new Intent(CardsActivity.this, OfferActivity.class));
                                return true;

                            case R.id.action_signup:
                                startActivity(new Intent(CardsActivity.this, SignUpActivity.class));
                                return true;

                            case R.id.action_createshop:
                                startActivity(new Intent(CardsActivity.this, ShopActivity.class));
                                return true;

                            case R.id.action_go_to_home:
                                startActivity(new Intent(CardsActivity.this, CardsActivity.class));
                                return true;

                            default:
                                Toast.makeText(getApplicationContext(), "item clicked", Toast.LENGTH_SHORT).show();
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
                Snackbar.make(v, "Hello Snackbar!", Snackbar.LENGTH_LONG).show();
                //  startActivity(new Intent(CardsActivity.this,Add_NewAdd.class));
                // startActivity(new Intent(CardsActivity.this,LoginActivity.class));
            }
        });
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());

        Bundle arguments1 = new Bundle();
        // arguments1.putString("ref",ref1);


        CardsFragment cardContentFragment1 = new CardsFragment();
        if (!cardContentFragment1.isAdded()) {
            cardContentFragment1.setArguments(arguments1);
        }
        adapter.addFragment(cardContentFragment1, "الأحدث");

        Bundle arguments2 = new Bundle();
        // arguments2.putString("ref", ref2);

        CardsFragment cardContentFragment2 = new CardsFragment();
        if (!cardContentFragment2.isAdded()) {
            cardContentFragment2.setArguments(arguments2);
        }
        adapter.addFragment(cardContentFragment2, "الأكثر مشاهده");


        Bundle arguments3 = new Bundle();
        // arguments3.putString("ref", ref3);

        CardsFragment cardContentFragment3 = new CardsFragment();
        if (!cardContentFragment3.isAdded()) {
            cardContentFragment3.setArguments(arguments3);
        }
        adapter.addFragment(cardContentFragment3, "عروض مميزه");


        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
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
