package com.app3roodk.UI.DetailActivity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.app3roodk.R;
import com.app3roodk.UI.Signing.SignUpFragment;


public class DetailActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState == null) {
            DetailFragment fragment = new DetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_content, fragment)
                    .commit();
        }
    }
}
