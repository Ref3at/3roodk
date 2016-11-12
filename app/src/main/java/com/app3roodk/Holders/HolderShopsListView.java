package com.app3roodk.Holders;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app3roodk.R;

public class HolderShopsListView {

    private Button btnDelete, btnActive;
    private TextView txtInactive, txtShopName;
    private Context mContext;

    public HolderShopsListView(View rootView, Context context) {
        mContext = context;
        init(rootView);
    }

    private void init(View rootView) {
        btnDelete = (Button) rootView.findViewById(R.id.btn_delete_shop);
        btnActive = (Button) rootView.findViewById(R.id.btn_activate);
        txtInactive = (TextView) rootView.findViewById(R.id.txt_inactive);
        txtShopName = (TextView) rootView.findViewById(R.id.txt_shop_name);
    }

    public void setShopName(String name) {
        txtShopName.setText(name);
    }

    public void setShopActive(boolean isShopActive) {
        if (!isShopActive) {
            txtInactive.setVisibility(View.VISIBLE);
            btnActive.setVisibility(View.VISIBLE);
        } else {
            txtInactive.setVisibility(View.GONE);
            btnActive.setVisibility(View.GONE);
        }
    }

    public void setDeleteClickListener(View.OnClickListener listener) {
        btnDelete.setOnClickListener(listener);
    }

    public void setActiveClickListener(View.OnClickListener listener) {
        btnActive.setOnClickListener(listener);
    }
}
