package com.app3roodk.UI.PositioningActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.app3roodk.Constants;
import com.app3roodk.R;
import com.app3roodk.UI.CategoriesActivity.CategoriesActivity;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PositioningActivity extends AppCompatActivity {

    @Bind(R.id.gov_spinner)
    BetterSpinner gov_spinner;

    @Bind(R.id.city_spinner)
    BetterSpinner city_spinner;
    Button go;
    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mSharedPrefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positioning);
        ButterKnife.bind(this);

        mSharedPref = getBaseContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mSharedPrefEditor = mSharedPref.edit();
        go = (Button) findViewById(R.id.go_btn);


        String[] list = getResources().getStringArray(R.array.governates);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, list);

        gov_spinner.setAdapter(adapter);


        gov_spinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                city_spinner.setVisibility(View.VISIBLE);

            }
        });


        //mSharedPrefEditor.putString(Constants.KEY_CURRENT_LATITUDE, "currentlat").apply();
        //  mSharedPrefEditor.putString(Constants.KEY_CURRENT_LONGITUDE, "currentlon").apply();


        String[] list2 = getResources().getStringArray(R.array.cities);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, list2);

        city_spinner.setAdapter(adapter2);

        city_spinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                go.setVisibility(View.VISIBLE);
            }
        });


    }

    public void go(View view) {
        mSharedPrefEditor.putString(Constants.KEY_CURRENT_GOVERNATE, gov_spinner.getText().toString()).apply();
        mSharedPrefEditor.putString(Constants.KEY_CURRENT_CITY, city_spinner.getText().toString()).apply();


        startActivity(new Intent(PositioningActivity.this, CategoriesActivity.class));
        finish();

    }

    public void getlocation(View view) {

        Toast.makeText(this, "get from maps", Toast.LENGTH_SHORT).show();
    }
}
