package com.app3roodk.UI.CategoriesActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
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
import com.app3roodk.UI.OffersCards.CardsActivity;
import com.app3roodk.UI.Shop.ShopActivity;
import com.app3roodk.UI.Signing.SignUpActivity;

public class CategoriesActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);


        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Categories");
        setSupportActionBar(toolbar);


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
                                startActivity(new Intent(CategoriesActivity.this, OfferActivity.class));
                                return true;

                            case R.id.action_signup:
                                startActivity(new Intent(CategoriesActivity.this, SignUpActivity.class));
                                return true;

                            case R.id.action_createshop:
                                startActivity(new Intent(CategoriesActivity.this, ShopActivity.class));
                                return true;

                            default:
                                Toast.makeText(getApplicationContext(), "item clicked", Toast.LENGTH_SHORT).show();
                                mDrawerLayout.closeDrawers();
                                return true;

                        }



                    }
                });

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

    public void gooo(View view) {
        int x = view.getId();
        //   Intent i = new Intent(Menu.this, Test.class);
        Intent i = new Intent(CategoriesActivity.this, CardsActivity.class);
        switch (x) {

            case R.id.imageButton1:

                i.putExtra("ref", "https://ads-app.firebaseio.com/cat/resturant");
                i.putExtra("titl", "Restaurants");


                break;
            case R.id.imageButton2:


                i.putExtra("ref", "https://ads-app.firebaseio.com/cat/resturant");
                i.putExtra("titl", "مطاعم");


                break;
            case R.id.imageButton3:

                i.putExtra("ref", "https://ads-app.firebaseio.com/cat/resturant");
                i.putExtra("titl", "Electrical Tools");


                break;
            case R.id.imageButton4:

                i.putExtra("ref", "https://ads-app.firebaseio.com/cat/resturant");
                i.putExtra("titl", "Mobiles");


                break;
            case R.id.imageButton5:

                i.putExtra("ref", "https://ads-app.firebaseio.com/cat/resturant");
                i.putExtra("titl", "Computers");


                break;
            case R.id.imageButton6:

                i.putExtra("ref", "https://ads-app.firebaseio.com/cat/resturant");
                i.putExtra("titl", "Shoes");


                break;
            case R.id.imageButton7:

                i.putExtra("ref", "https://ads-app.firebaseio.com/cat/resturant");
                i.putExtra("titl", "Clothes");


                break;
            case R.id.imageButton8:


                i.putExtra("ref", "https://ads-app.firebaseio.com/cat/resturant");
                i.putExtra("titl", "Super market");


                break;
            case R.id.imageButton9:

                i.putExtra("ref", "https://ads-app.firebaseio.com/cat/resturant");
                i.putExtra("titl", "خدمات");


                break;
        }

        CategoriesActivity.this.startActivity(i);
    }

    @Override
    public void onBackPressed() {
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
