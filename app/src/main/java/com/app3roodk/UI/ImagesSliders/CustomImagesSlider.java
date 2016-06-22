package com.app3roodk.UI.ImagesSliders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app3roodk.R;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;

/**
 * Created by Refaat on 6/22/2016.
 */
public class CustomImagesSlider extends BaseSliderView {

    private String offer_price_before;
    private String offer_price_after;
    private String offer_sale_perc;

    public String getOffer_price_before() {
        return offer_price_before;
    }

    public void setOffer_price_before(String offer_price_before) {
        this.offer_price_before = offer_price_before;
    }

    public String getOffer_sale_perc() {
        return offer_sale_perc;
    }

    public void setOffer_sale_perc(String offer_sale_perc) {
        this.offer_sale_perc = offer_sale_perc;
    }

    public String getOffer_price_after() {
        return offer_price_after;
    }

    public void setOffer_price_after(String offer_price_after) {
        this.offer_price_after = offer_price_after;
    }

    public CustomImagesSlider(Context context) {
        super(context);
    }

    @Override
    public View getView() {

        View v = LayoutInflater.from(getContext()).inflate(R.layout.custom_offer_slider_layout, null);
        ImageView target = (ImageView) v.findViewById(R.id.daimajia_slider_image);
        TextView description = (TextView) v.findViewById(R.id.description);
        description.setText(getDescription());

        TextView textViewPriceBefore = (TextView) v.findViewById(R.id.price_before);
        textViewPriceBefore.setText(getOffer_price_before());

        TextView textViewPriceAfter = (TextView) v.findViewById(R.id.price_after);
        textViewPriceAfter.setText(getOffer_price_after());

        TextView textViewSalePersent = (TextView) v.findViewById(R.id.sale_percent);
        textViewSalePersent.setText(getOffer_sale_perc());

        bindEventAndShow(v, target);
        return v;
    }


}
