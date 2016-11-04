package com.app3roodk.UI.DetailActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.app3roodk.R;
import com.app3roodk.Schema.OfferMgallat;
import com.app3roodk.UI.FullScreenImage.FullScreenImageSlider;
import com.app3roodk.UI.ImagesSliders.CustomImagesSliderHypers;
import com.app3roodk.UtilityGeneral;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DetailOfflineHyperFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    //region Member Variables
    final static long minutesInMilli = 1000 * 60;
    final static long hoursInMilli = minutesInMilli * 60;
    final static long daysInMilli = hoursInMilli * 24;
    public OfferMgallat offer;
    Button btnShopWay;
    TextView txtViews, txtDay, txtHour, txtMinute, txtDescription, txtDistance;
    SliderLayout mSlider;
    Calendar cal;
    long timeInMilliseconds;
    Thread timer;
    //endregion
    Runnable updateTimerThread = new Runnable() {
        public void run() {
            while (true) {
                timeInMilliseconds = cal.getTime().getTime() - System.currentTimeMillis();
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtDay.setText(String.valueOf((int) (timeInMilliseconds / daysInMilli)));
                            timeInMilliseconds = timeInMilliseconds % daysInMilli;
                            txtHour.setText(String.valueOf((int) (timeInMilliseconds / hoursInMilli)));
                            timeInMilliseconds = timeInMilliseconds % hoursInMilli;
                            txtMinute.setText(String.valueOf((int) (timeInMilliseconds / minutesInMilli)));
                        }
                    });
                    Thread.sleep(30000);
                } catch (Exception ex) {
                    break;
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_hyper, container, false);
        init(rootView);
        fillViews();
        initSlider(offer.getLstImages(), rootView);
        clickConfig();
        return rootView;
    }


    private void fillViews() {
        try {
            txtDescription.setText(offer.getDesc());
            txtViews.setText(String.valueOf(offer.getViewNum()));
            if (UtilityGeneral.getCurrentLonAndLat(getActivity()) != null) {
                txtDistance.setText(String.valueOf(UtilityGeneral.calculateDistanceInKM(
                        Double.parseDouble(offer.getLat()),
                        Double.parseDouble(offer.getLon()),
                        UtilityGeneral.getCurrentLonAndLat(getActivity()).latitude,
                        UtilityGeneral.getCurrentLonAndLat(getActivity()).longitude)) + "km");
            } else {
                txtDistance.setVisibility(View.INVISIBLE);
            }
        } catch (Exception ex) {
        }
    }

    private void clickConfig() {
        btnShopWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(UtilityGeneral.DrawPathToCertainShop(offer.getLat(), offer.getLon()));
            }
        });
    }

    private void init(View rootView) {
        try {
            mSlider = (SliderLayout) rootView.findViewById(R.id.imgOffer);
            btnShopWay = (Button) rootView.findViewById(R.id.btnShopWay);
            txtViews = (TextView) rootView.findViewById(R.id.txtViews);
            txtDay = (TextView) rootView.findViewById(R.id.txtDay);
            txtHour = (TextView) rootView.findViewById(R.id.txtHour);
            txtMinute = (TextView) rootView.findViewById(R.id.txtMinute);
            txtDescription = (TextView) rootView.findViewById(R.id.txtDesc);
            txtDistance = (TextView) rootView.findViewById(R.id.txtDistance);
            offer = new Gson().fromJson(getActivity().getIntent().getStringExtra("offer"), OfferMgallat.class);
            cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, Integer.parseInt(offer.getEndTime().substring(0, 4)));
            cal.set(Calendar.MONTH, Integer.parseInt(offer.getEndTime().substring(4, 6)) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(offer.getEndTime().substring(6, 8)));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(offer.getEndTime().substring(8, 10)));
            cal.set(Calendar.MINUTE, Integer.parseInt(offer.getEndTime().substring(10, 12)));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

        } catch (Exception ex) {
            try {
                getActivity().finish();
            } catch (Exception exx) {
            }
        }
    }

    private void initSlider(List<String> lstItems, View rootView) {

        for (int i = 0; i < lstItems.size(); i++) {

            CustomImagesSliderHypers customImagesSlider = new CustomImagesSliderHypers(getActivity());

            customImagesSlider
                    .image(lstItems.get(i))
                    .setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
                    .setOnSliderClickListener(this);
            mSlider.addSlider(customImagesSlider);
        }

        if (lstItems.size() == 1) {
            mSlider.stopAutoCycle();
            mSlider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
        } else {
            mSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOut);
            mSlider.setCustomIndicator((PagerIndicator) rootView.findViewById(R.id.custom_indicator));
            mSlider.setCustomAnimation(new DescriptionAnimation());
            mSlider.setDuration(5000);
        }
        mSlider.addOnPageChangeListener(this);
    }

    @Override
    public void onStop() {
        mSlider.stopAutoCycle();
        if (timer.isAlive())
            timer.interrupt();
        super.onStop();
    }

    @Override
    public void onResume() {
        try {
            if (offer.getLstImages().size() == 1) {
                mSlider.stopAutoCycle();
            } else {
                mSlider.startAutoCycle();
            }
        } catch (Exception ex) {
        }
        ;
        timer = new Thread(updateTimerThread);
        timer.start();
        super.onResume();
    }

    private void showMessage(String msg) {
        Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

//        Log.d("Slider", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Intent intent = new Intent(getActivity(), FullScreenImageSlider.class);

        ArrayList<String> imgs = new ArrayList<>();
        for (int i = mSlider.getCurrentPosition(); i < offer.getLstImages().size(); i++)
            imgs.add(offer.getLstImages().get(i));
        for (int i = 0; i < mSlider.getCurrentPosition(); i++)
            imgs.add(offer.getLstImages().get(i));
        intent.putStringArrayListExtra("IMAGES", imgs);
        startActivity(intent);
    }
}
