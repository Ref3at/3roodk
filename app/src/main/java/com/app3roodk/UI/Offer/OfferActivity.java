package com.app3roodk.UI.Offer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.app3roodk.R;

public class OfferActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_offer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.add_offer_container, new AddNewOfferFragment())
                    .commit();
        }
    }
}
