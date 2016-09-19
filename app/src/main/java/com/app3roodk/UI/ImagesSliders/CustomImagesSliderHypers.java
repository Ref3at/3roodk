package com.app3roodk.UI.ImagesSliders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.app3roodk.R;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;

/**
 * Created by Refaat on 6/22/2016.
 */
public class CustomImagesSliderHypers extends BaseSliderView {

    public CustomImagesSliderHypers(Context context) {
        super(context);
    }

    @Override
    public View getView() {

        View v = LayoutInflater.from(getContext()).inflate(R.layout.custom_offer_hyper_slider_layout, null);
        ImageView target = (ImageView) v.findViewById(R.id.daimajia_slider_image);

        bindEventAndShow(v, target);
        return v;
    }
}
