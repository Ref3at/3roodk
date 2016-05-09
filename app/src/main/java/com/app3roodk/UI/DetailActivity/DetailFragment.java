package com.app3roodk.UI.DetailActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app3roodk.R;
import com.app3roodk.UI.FullScreenImage.FullScreenImageSlider;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ZooM- on 5/9/2016.
 */
public class DetailFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    Button btnFavorite, btnShare, btnShopWay;
    TextView txtViews, txtSale, txtPriceBefore, txtPriceAfter, txtDay, txtHour, txtMinute, txtDescription,
            txtShopName, txtWorkTime, txtAddress, txtMobile, txtRate;
    RatingBar ratebar;
    private SliderLayout mDemoSlider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        init(rootView);
        initSlider();
        return rootView;
    }

    private void init(View rootView) {

        mDemoSlider = (SliderLayout) rootView.findViewById(R.id.imgOffer);


        btnFavorite = (Button) rootView.findViewById(R.id.btnFavorite);
        btnShare = (Button) rootView.findViewById(R.id.btnShare);
        btnShopWay = (Button) rootView.findViewById(R.id.btnShopWay);
        txtViews = (TextView) rootView.findViewById(R.id.txtViews);
        txtSale = (TextView) rootView.findViewById(R.id.txtSale);
        txtPriceBefore = (TextView) rootView.findViewById(R.id.txtPriceBefore);
        txtPriceAfter = (TextView) rootView.findViewById(R.id.txtPriceAfter);
        txtDay = (TextView) rootView.findViewById(R.id.txtDay);
        txtHour = (TextView) rootView.findViewById(R.id.txtHour);
        txtMinute = (TextView) rootView.findViewById(R.id.txtMinute);
        txtDescription = (TextView) rootView.findViewById(R.id.txtDesc);
        txtShopName = (TextView) rootView.findViewById(R.id.txtShopName);
        txtWorkTime = (TextView) rootView.findViewById(R.id.txtWorkTime);
        txtAddress = (TextView) rootView.findViewById(R.id.txtAddress);
        txtMobile = (TextView) rootView.findViewById(R.id.txtMobile);
        txtRate = (TextView) rootView.findViewById(R.id.txtRateNumber);
        ratebar = (RatingBar) rootView.findViewById(R.id.ratingbar);
    }

    private void initSlider() {
        HashMap<String, String> url_maps = new HashMap<String, String>();
        url_maps.put("زيت عافيه", "http://i.imgur.com/ZBD2l9g.jpg");
        url_maps.put("برسيل", "http://i.imgur.com/dSGdt6y.jpg");
        url_maps.put("صن شاين", "http://i.imgur.com/MwSD40z.jpg");
        url_maps.put("الرشيدى الميزان", "http://i.imgur.com/8tRO2Gi.jpg");


        for (String name : url_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(getActivity());
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }


        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Stack);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
    }

    @Override
    public void onStop() {
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onResume() {
        mDemoSlider.startAutoCycle();
        super.onResume();

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        ArrayList<String> imgs_ids = new ArrayList<>();

        imgs_ids.add("http://i.imgur.com/8tRO2Gi.jpg");
        imgs_ids.add("http://i.imgur.com/MwSD40z.jpg");
        imgs_ids.add("http://i.imgur.com/dSGdt6y.jpg");
        imgs_ids.add("http://i.imgur.com/ZBD2l9g.jpg");


        Toast.makeText(getActivity(), "" + slider.getUrl(), Toast.LENGTH_LONG).show();

        Intent i = new Intent(getActivity(), FullScreenImageSlider.class);
        // we need to open this image in image viewer
        i.putStringArrayListExtra("IMAGES", imgs_ids);

        startActivity(i);


    }
}
