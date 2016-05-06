package com.app3roodk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.app3roodk.UI.CategoriesActivity.CategoriesActivity;
import com.app3roodk.UI.PositioningActivity.PositioningActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences sharedPref = getBaseContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);


        if ((sharedPref.contains(Constants.KEY_CURRENT_LATITUDE) && sharedPref.contains(Constants.KEY_CURRENT_LONGITUDE))
                ||
                (sharedPref.contains(Constants.KEY_CURRENT_GOVERNATE) && sharedPref.contains(Constants.KEY_CURRENT_CITY))) {


            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, CategoriesActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }, 500);
        } else {

            final Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, PositioningActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }, 500);
        }
    }

}
