package com.app3roodk.UI.FavoritesDetails;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app3roodk.R;
import com.app3roodk.UI.DetailActivity.DetailFragment;

public class FavoritesDetailActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_detail);
        if (savedInstanceState == null) {
            FavoritesDetailFragment fragment = new FavoritesDetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.favorites_detail_content, fragment)
                    .commit();
        }
    }
}
