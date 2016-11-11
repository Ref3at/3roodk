package com.app3roodk.UI.DetailActivity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app3roodk.Constants;
import com.app3roodk.R;
import com.app3roodk.Utilities.UtilityViews;

public class DetailActivity extends AppCompatActivity {

    DetailOfflineFragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState == null) {
            if (getIntent().getExtras().getInt("details") == Constants.DETAILS_ONLINE) {
                DetailOnlineFragment fragment = new DetailOnlineFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_content, fragment)
                        .commit();
            } else if (getIntent().getExtras().getInt("details") == Constants.DETAILS_OFFLINE_SHOPS) {
                mFragment = new DetailOfflineFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_content, mFragment)
                        .commit();
            } else {
                DetailOfflineHyperFragment fragment = new DetailOfflineHyperFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_content, fragment)
                        .commit();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_SIGN_IN)
            UtilityViews.signingResult(resultCode, this, mFragment);
    }
}
