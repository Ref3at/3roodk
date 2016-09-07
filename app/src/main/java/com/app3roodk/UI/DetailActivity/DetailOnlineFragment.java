package com.app3roodk.UI.DetailActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app3roodk.R;
import com.app3roodk.Schema.Item;
import com.app3roodk.Schema.Offer;
import com.app3roodk.UI.FullScreenImage.FullScreenImageSlider;
import com.app3roodk.UI.ImagesSliders.CustomImagesSlider;
import com.app3roodk.UtilityFirebase;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailOnlineFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    //region Member Variables

    ImageButton btnShare;

    Button btnGoToPayment;

    TextView txtViews, txtSale, txtPriceBefore, txtPriceAfter,
            txtShopName, txtRate;
    SliderLayout mSlider;
    ExpandableHeightListView lsvDesc;
    public Offer offer;

    ValueEventListener offerListener;
    DatabaseReference drOffer;
    //endregion

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_online, container, false);
        init(rootView);
        fillViews();
        initSlider(offer.getItems(), rootView);
        makeFirebaseReferences();
        clickConfig();
        return rootView;
    }

    private void fillViews() {
        try {
            txtShopName.setText(offer.getShopName());
            txtViews.setText(String.valueOf(offer.getViewNum()));
            txtRate.setText(String.valueOf(String.valueOf(offer.getAverageRate())));
        } catch (Exception ex) {
        }
    }

    private void clickConfig() {
        btnGoToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(offer.getCity())));
            }
        });
    }

    private void makeFirebaseReferences() {
        try {
            drOffer = UtilityFirebase.getOffer(offer);
            drOffer.addValueEventListener(offerListener);
        } catch (Exception ex) {
        }
    }

    private void init(View rootView) {
        try {
            mSlider = (SliderLayout) rootView.findViewById(R.id.imgOffer);
            btnShare = (ImageButton) rootView.findViewById(R.id.btnShare);
            btnGoToPayment = (Button) rootView.findViewById(R.id.btnShopWay);
            lsvDesc = (ExpandableHeightListView) rootView.findViewById(R.id.lsvDesc);
            txtViews = (TextView) rootView.findViewById(R.id.txtViews);
            txtSale = (TextView) rootView.findViewById(R.id.txtSale);
            txtPriceBefore = (TextView) rootView.findViewById(R.id.txtPriceBefore);
            txtPriceAfter = (TextView) rootView.findViewById(R.id.txtPriceAfter);
            txtShopName = (TextView) rootView.findViewById(R.id.txtShopName);
            txtRate = (TextView) rootView.findViewById(R.id.txtRateNumber);
            offer = new Gson().fromJson(getActivity().getIntent().getStringExtra("offer"), Offer.class);
            HashMap<String, String> data = new Gson().fromJson(offer.getDesc(), new TypeToken<HashMap<String, String>>() {
            }.getType());
            ArrayList<String> lstKeys = new ArrayList<>();
            for (Map.Entry<String, String> entry : data.entrySet())
                lstKeys.add(entry.getKey());
            DescAdapter adapter = new DescAdapter(getActivity(), R.layout.item_desc, data, lstKeys);
            lsvDesc.setAdapter(adapter);
            lsvDesc.setExpanded(true);
            offerListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    offer = dataSnapshot.getValue(Offer.class);
                    fillViews();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
        } catch (Exception ex) {
            try {
                getActivity().finish();
            } catch (Exception exx) {
            }
        }
    }

    private void initSlider(List<Item> lstItems, View rootView) {

        for (int i = 0; i < lstItems.size(); i++) {

            for (int j = 0; j < lstItems.get(i).getImagePaths().size(); j++) {

                CustomImagesSlider customImagesSlider = new CustomImagesSlider(getActivity());
                customImagesSlider.setOffer_price_before(offer.getItems().get(i).getPriceBefore());
                customImagesSlider.setOffer_price_after(offer.getItems().get(i).getPriceAfter());
                customImagesSlider.setOffer_sale_perc(String.format("%.0f", (1 - (Double.parseDouble(offer.getItems().get(i).getPriceAfter())
                        / Double.parseDouble(offer.getItems().get(i).getPriceBefore()))) * 100) + "%");

                customImagesSlider
                        .description("حصرى على عروضك")
                        .image(lstItems.get(i).getImagePaths().get(j))
                        .setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
                        .setOnSliderClickListener(this);
                mSlider.addSlider(customImagesSlider);
            }
        }

        if (lstItems.size() == 1 && lstItems.get(0).getImagePaths().size() == 1) {
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
        super.onStop();
    }

    @Override
    public void onResume() {
        try {
            if (offer.getItems().size() == 1 && offer.getItems().get(0).getImagePaths().size() == 1) {
                mSlider.stopAutoCycle();
            } else {
                mSlider.startAutoCycle();
            }
        } catch (Exception ex) {
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            drOffer.removeEventListener(offerListener);
        } catch (Exception ex) {
        }
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
        ArrayList<String> lstImagePaths = new ArrayList<>();
        for (int i = 0; i < offer.getItems().size(); i++) {
            for (int j = 0; j < offer.getItems().get(i).getImagePaths().size(); j++) {
                lstImagePaths.add(offer.getItems().get(i).getImagePaths().get(j));
            }
        }

        ArrayList<String> imgs = new ArrayList<>();
        for (int i = mSlider.getCurrentPosition(); i < lstImagePaths.size(); i++)
            imgs.add(lstImagePaths.get(i));
        for (int i = 0; i < mSlider.getCurrentPosition(); i++)
            imgs.add(lstImagePaths.get(i));
        intent.putStringArrayListExtra("IMAGES", imgs);
        startActivity(intent);
    }

    class DescAdapter extends ArrayAdapter {

        Context context;
        int layoutResourceId;

        HashMap<String, String> data = null;
        ArrayList<String> Keys;

        public DescAdapter(Context context, int layoutResourceId, HashMap<String, String> data, ArrayList<String> lstKeys) {
            super(context, layoutResourceId, lstKeys);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
            this.Keys = lstKeys;
        }

        @Override
        public View getView(int position, final View convertView, final ViewGroup parent) {
            View row = convertView;
            final DescHolder holder;
            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new DescHolder(row);
                row.setTag(holder);
            } else {
                holder = (DescHolder) row.getTag();
            }
            holder.key.setText(Keys.get(position));
            holder.value.setText(data.get(Keys.get(position)));
            return row;
        }
    }

    class DescHolder {
        public TextView key, value;

        public DescHolder(View rootView) {
            key = (TextView) rootView.findViewById(R.id.txtKey);
            value = (TextView) rootView.findViewById(R.id.txtValue);

        }

    }


}
