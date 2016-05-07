package com.app3roodk.UI.Signing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.app3roodk.R;

/**
 * Created by ZooM- on 5/7/2016.
 */

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.new_user));
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            SignUpFragment fragment = new SignUpFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.signup_container, fragment)
                    .commit();
        }
    }
}
