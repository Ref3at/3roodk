package com.app3roodk.UI.Signing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.app3roodk.R;

/**
 * Created by ZooM- on 5/7/2016.
 */

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.sign_in));
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            SignInFragment fragment = new SignInFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.signin_container, fragment)
                    .commit();
        }
    }
}
