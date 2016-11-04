package com.app3roodk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.app3roodk.UI.IntroActivity.NewIntroActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilityGeneral.initGoogleApiClient(getBaseContext());
        UtilityGeneral.mGoogleApiClient.connect();

        final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, NewIntroActivity.class));
                // startActivity(new Intent(SplashActivity.this, FeedbackActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 400);
    }

}
