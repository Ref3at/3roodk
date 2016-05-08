package com.app3roodk.UI.Shop;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app3roodk.R;

/**
 * Created by Refaat on 5/6/2016.
 */
public class CreateShopFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.creatshop_layout, container, false);

        return rootView;
    }
}
