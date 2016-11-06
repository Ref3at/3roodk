package com.app3roodk.UI.MainActivity;

/**
 * Created by ultra book on 10/12/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app3roodk.R;
import com.app3roodk.Schema.Shop;
import com.app3roodk.UI.Shop.ViewShopActivity;
import com.app3roodk.UtilityFirebase;
import com.app3roodk.UtilityGeneral;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;


/**
 * Created by ultra book on 10/12/2016.
 */

public class AllShopsFragment extends Fragment {

    public static String SelectedCity;
    public static String SelectedGov;


    SpinKitView progress;
    ExpandableHeightGridView gridView;
    ValueEventListener shopsListeners;
    private SampleAdapter adapter;
    private ArrayList<Shop> lstAllShops;
    private Query qrShopsOffers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_all_shops, container, false);
        init(rootView);
        return rootView;
    }


    private void init(View rootView) {
        try {
            if (!UtilityGeneral.mGoogleApiClient.isConnected() && !UtilityGeneral.mGoogleApiClient.isConnecting())
                UtilityGeneral.mGoogleApiClient.connect();
        } catch (Exception ignored) {
        }
        progress = (SpinKitView) rootView.findViewById(R.id.progress);
        gridView = (ExpandableHeightGridView) rootView.findViewById(R.id.grid_view);
        lstAllShops = new ArrayList<>();
        adapter = new SampleAdapter(getActivity(), R.layout.item_hyper, lstAllShops);
        gridView.setAdapter(adapter);
        gridView.setExpanded(true);
        clickConfig();
        initListener();
        getAllShops();
    }

    private void getAllShops() {

        progress.setVisibility(View.VISIBLE);
        try {
            qrShopsOffers.removeEventListener(shopsListeners);
        } catch (Exception ignored) {
        }
        qrShopsOffers = UtilityFirebase.getAllCityShops(SelectedGov, SelectedCity);
        qrShopsOffers.addValueEventListener(shopsListeners);


    }


    private void initListener() {

        shopsListeners = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lstAllShops.clear();
                progress.setVisibility(View.GONE);

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Shop shop = postSnapshot.getValue(Shop.class);
                    lstAllShops.add(shop);
                }

                if (lstAllShops.size() == 0)
                    showMessage("لا يوجد محلات فى مدينتك الحاليه");
                adapter.notifyDataSetChanged();

                try {
                    qrShopsOffers.removeEventListener(shopsListeners);
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progress.setVisibility(View.GONE);
                showMessage("بص على النت كده");
            }
        };


    }

    private void clickConfig() {

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Shop shop = (Shop) lstAllShops.get(i);
                if (shop != null) {
                    Intent intent = new Intent(getActivity(), ViewShopActivity.class);
                    intent.putExtra("shop", new Gson().toJson(shop));
                    view.getContext().startActivity(intent);
                }
            }
        });

    }

    private void showMessage(String msg) {
        if (isAdded() && !isDetached() && isVisible())
            Snackbar.make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
    }


    class SampleAdapter extends ArrayAdapter {

        private LayoutInflater mLayoutInflater;
        private ArrayList<Shop> lstShopssAll;


        SampleAdapter(Context context, int textViewResourceId,
                      ArrayList<Shop> objects) {
            super(context, textViewResourceId);
            this.mLayoutInflater = LayoutInflater.from(context);
            this.lstShopssAll = objects;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView,
                            @NonNull ViewGroup parent) {

            final ViewHolder holder;
            if (convertView == null) {

                convertView = mLayoutInflater.inflate(R.layout.item_hyper, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Shop shop = lstShopssAll.get(position);
            holder.txtView.setText(shop.getName());

            if (shop.getLogoId() == null) {
                Glide.with(getActivity()).load(R.drawable.defaultavatar).asBitmap().into(new BitmapImageViewTarget(holder.imgView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.imgView.setImageDrawable(circularBitmapDrawable);
                    }
                });
            } else {
                Glide.with(getActivity()).load(shop.getLogoId()).asBitmap().into(new BitmapImageViewTarget(holder.imgView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.imgView.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }

            return convertView;
        }


        @Override
        public int getCount() {
            return lstShopssAll.size();
        }


        class ViewHolder {
            ImageView imgView;
            TextView txtView;

            ViewHolder(View rootView) {
                imgView = (ImageView) rootView.findViewById(R.id.imgLogo);
                txtView = (TextView) rootView.findViewById(R.id.txttitle);
            }
        }

    }
}
