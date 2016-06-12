package com.app3roodk.UI.Shop;


import android.location.Address;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app3roodk.R;
import com.app3roodk.Schema.Shop;
import com.app3roodk.UtilityGeneral;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Refaat on 5/6/2016.
 */
public class ViewShopFragment extends Fragment {

    static public LatLng latLngShop;
    Shop shop;
    List<Address> addresses;


    TextView txtShopName, txtWorkingTime, AddressFromMap, txtAdressInfo, txtContacts;
    ImageView imageLogo;
    Button btnShopWay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_shop_layout, container, false);

        if (getActivity().getIntent().getExtras() != null) {
            shop = new Gson().fromJson(getActivity().getIntent().getStringExtra("shop"), Shop.class);
        } else {

            shop = UtilityGeneral.loadShop(getActivity());
        }

        if (shop.getObjectId() == UtilityGeneral.loadShop(getActivity()).getObjectId()) {
            Toast.makeText(getActivity(), "انت صاحب المحل", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "مش انت صاحب المحل", Toast.LENGTH_SHORT).show();

        }

        latLngShop = new LatLng(Double.parseDouble(shop.getLat()), Double.parseDouble(shop.getLon()));


        init(rootView);
        fillViews();


        btnShopWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(UtilityGeneral.DrawPathToCertainShop(
                        getContext(), UtilityGeneral.getCurrentLonAndLat(getContext()),
                        latLngShop));
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        writeAddress();
    }


    private void writeAddress() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<Address> tempAddresses = UtilityGeneral.getCurrentGovAndCity(getActivity(), latLngShop);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                AddressFromMap.setText(tempAddresses.get(0).getAddressLine(3) + " - " + tempAddresses.get(0).getAddressLine(2) + " - " + tempAddresses.get(0).getAddressLine(1) + " - " + tempAddresses.get(0).getAddressLine(0));
                            } catch (Exception ex) {
                                Log.e("CreateShopFragment", ex.getMessage());
                            }
                        }
                    });
                    addresses = UtilityGeneral.getCurrentGovAndCityInEnglish(getActivity(), latLngShop);
                }
            }).start();
        } catch (Exception ex) {
        }
    }

    private void fillViews() {

        if (shop.getLogoId() == null) {
            imageLogo.setVisibility(View.GONE);
        } else {
            Picasso.with(getActivity()).load(shop.getLogoId()).into(imageLogo);
        }


        txtShopName.setText(shop.getName());
        txtWorkingTime.setText(shop.getWorkingTime());
        txtAdressInfo.setText(shop.getAddress());
        txtContacts.setText(shop.getContacts());

    }

    private void init(View rootView) {

        imageLogo = (ImageView) rootView.findViewById(R.id.v_logo);
        txtShopName = (TextView) rootView.findViewById(R.id.v_shopname);
        txtWorkingTime = (TextView) rootView.findViewById(R.id.v_workingtime);
        AddressFromMap = (TextView) rootView.findViewById(R.id.v_txtShopAddressFromMap);
        txtAdressInfo = (TextView) rootView.findViewById(R.id.v_addressinfo);
        txtContacts = (TextView) rootView.findViewById(R.id.v_contacts);

        btnShopWay = (Button) rootView.findViewById(R.id.v_btnShopWay);


    }
}
