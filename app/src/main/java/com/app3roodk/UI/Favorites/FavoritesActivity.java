package com.app3roodk.UI.Favorites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.app3roodk.R;
import com.app3roodk.UI.DetailActivity.DetailFragment;
import com.app3roodk.UI.Offer.AddNewOfferFragment;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("العروض المُفضله");
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.favorites_container, new FavoritesFragment())
                    .commit();
        }
    }
}