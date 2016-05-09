package com.app3roodk.UI.DetailActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.app3roodk.R;

/**
 * Created by ZooM- on 5/9/2016.
 */
public class DetailFragment extends Fragment {

    Button btnFavorite, btnShare, btnShopWay;
    TextView txtViews, txtSale, txtPriceBefore, txtPriceAfter, txtDay, txtHour, txtMinute, txtDescription,
            txtShopName, txtWorkTime, txtAddress, txtMobile, txtRate;
    RatingBar ratebar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
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
}
