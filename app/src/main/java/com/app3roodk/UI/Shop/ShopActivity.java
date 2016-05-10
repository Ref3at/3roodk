package com.app3roodk.UI.Shop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app3roodk.R;

public class ShopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.create_shop_container, new CreateShopFragment())
                    .commit();
        }
    }
}
