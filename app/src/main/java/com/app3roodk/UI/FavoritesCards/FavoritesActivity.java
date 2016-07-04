package com.app3roodk.UI.FavoritesCards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.app3roodk.R;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.favorites_container, new FavoritesFragment())
                    .commit();
        }
    }
}