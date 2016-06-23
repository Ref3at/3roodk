package com.app3roodk.UI.Shop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.app3roodk.R;

public class ListShopsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_shops);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("محلاتي");
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            ListShopsFragment fragment = new ListShopsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.lstShops_container, fragment)
                    .commit();
        }
    }
}
