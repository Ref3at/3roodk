package com.app3roodk.UI.IntroActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app3roodk.UI.MainActivity.MainActivity;
import com.app3roodk.UtilityGeneral;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroViewPager;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by ultra book on 16-Oct-16.
 */

public class NewIntroActivity extends AppIntro {

    static String theCity;
    static AppIntroViewPager viewPager;
    static IntroScreen5 introScreen5;
    Context mContext;
    int mRequestCode = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Note here that we DO NOT use setContentView();
        try {
            if (!UtilityGeneral.mGoogleApiClient.isConnected() && !UtilityGeneral.mGoogleApiClient.isConnecting())
                UtilityGeneral.mGoogleApiClient.connect();
        } catch (Exception ex) {
        }

        mContext = this;
        viewPager = this.getPager();

        setDoneText("تم");
        doneButton.setVisibility(View.INVISIBLE);
        introScreen5 = new IntroScreen5();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(new IntoScreen1());
        addSlide(new IntoScreen2());
        addSlide(new IntoScreen3());
        addSlide(new IntoScreen4FindingCity());

        addSlide(introScreen5);


        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        // addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);


        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }


    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.

        Intent i = new Intent(NewIntroActivity.this, MainActivity.class);
        startActivity(i);
        finish();

    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mRequestCode = requestCode;
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case IntoScreen4FindingCity.REQUEST_CHECK_SETTINGS:

                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new FindCity().execute();
                            }
                        }, 5000);


                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        Toast.makeText(this, "يمكنك اختيارها مانيوال", Toast.LENGTH_SHORT).show();
                        IntoScreen4FindingCity.choseFromList.setVisibility(View.VISIBLE);

                        break;
                }
                break;

            case IntoScreen4FindingCity.REQUEST_CHOSE_CITY:

                if (resultCode == Activity.RESULT_OK) {
                    String result = data.getStringExtra("result");
                    UtilityGeneral.saveCity(mContext, result);
                    introScreen5.updatingCity(result);
                    theCity = result;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem(4, true);

                        }
                    }, 1000);
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "لم يتم اختيار تغيير المدينه", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestCode != IntoScreen4FindingCity.REQUEST_CHOSE_CITY)
            if (!UtilityGeneral.loadCity(mContext).equals("No")) {
                startActivity(new Intent(NewIntroActivity.this, MainActivity.class));
                finish();
            }
    }

    public class FindCity extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(NewIntroActivity.this, "wait", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Void... params) {
            return UtilityGeneral.getCurrentCityArabic(mContext);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(mContext, "Done", Toast.LENGTH_SHORT).show();
            if (!s.equals("No")) {
                UtilityGeneral.saveCity(mContext, s);
                introScreen5.updatingCity(s);
                theCity = s;
                IntoScreen4FindingCity.createUser(mContext);
            } else {

                Toast.makeText(mContext, "حدث مشكله ف الgeo ورجع بـ " + s, Toast.LENGTH_SHORT).show();

            }
        }

    }
}
