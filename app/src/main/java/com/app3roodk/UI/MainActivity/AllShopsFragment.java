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

import com.app3roodk.Constants;
import com.app3roodk.R;
import com.app3roodk.Schema.Offer;
import com.app3roodk.Schema.Shop;
import com.app3roodk.UI.DetailActivity.DetailActivity;
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
import java.util.Iterator;


/**
 * Created by ultra book on 10/12/2016.
 */

public class AllShopsFragment extends Fragment {

    public static String SelectedCity;

    SpinKitView progress;
    ExpandableHeightGridView gridView;
    ValueEventListener[] shopsOfferListeners;
    String[] categoriesArray;
    private SampleAdapter adapter;
    private ArrayList<Offer> lstAllOffers;
    private Query[] qrShopsOffers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_all_shops, container, false);

        categoriesArray = getResources().getStringArray(R.array.cat_array_eng);
        qrShopsOffers = new Query[9];
        shopsOfferListeners = new ValueEventListener[9];
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
        lstAllOffers = new ArrayList<>();
        adapter = new SampleAdapter(getActivity(), R.layout.item_hyper, lstAllOffers);
        gridView.setAdapter(adapter);
        gridView.setExpanded(true);
        clickConfig();
        initListener();
        getAllOffers();
    }

    private void getAllOffers() {

        progress.setVisibility(View.VISIBLE);

        for (int i = 0; i < 9; i++) {
            try {
                qrShopsOffers[i].removeEventListener(shopsOfferListeners[i]);
            } catch (Exception ignored) {
            }
            qrShopsOffers[i] = UtilityFirebase.getActiveOffers(SelectedCity, categoriesArray[i]);
            qrShopsOffers[i].addValueEventListener(shopsOfferListeners[i]);

        }


    }


    private void initListener() {
        lstAllOffers.clear();

        for (int i = 0; i < 9; i++) {


            shopsOfferListeners[i] = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    adapter.notifyDataSetChanged();
                    progress.setVisibility(View.GONE);
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    Offer of;
                    DataSnapshot ds;
                    while (iterator.hasNext()) {
                        ds = iterator.next();
                        of = ds.getValue(Offer.class);
                        of.setObjectId(ds.getKey());
                        lstAllOffers.add(of);
                    }

//                    if (lstAllOffers.size() == 0)
//                        showMessage("لا يوجد عروض لهايبرات فى منطقتك الحاليه");
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progress.setVisibility(View.GONE);
                    showMessage("بص على النت كده");
                }
            };

        }
    }

    private void clickConfig() {

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                Offer offer = (Offer) lstAllOffers.get(i);

                Intent intent = new Intent(view.getContext(), DetailActivity.class);
                // FirebaseDatabase.getInstance().getReference("Offers").child(offer.getCity()).child(offer.getCategoryName()).child(offer.getObjectId()).child("viewNum").setValue(offer.getViewNum() + 1);
                offer.setViewNum(offer.getViewNum() + 1);
                intent.putExtra("offer", new Gson().toJson(offer));
                intent.putExtra("details", Constants.DETAILS_OFFLINE_SHOPS);
                view.getContext().startActivity(intent);


            }
        });

    }

    private void showMessage(String msg) {
        if (isAdded() && !isDetached() && isVisible())
            Snackbar.make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
    }


    class SampleAdapter extends ArrayAdapter {

        private LayoutInflater mLayoutInflater;
        private ArrayList<Offer> lstOffersAll;


        SampleAdapter(Context context, int textViewResourceId,
                      ArrayList<Offer> objects) {
            super(context, textViewResourceId);
            this.mLayoutInflater = LayoutInflater.from(context);
            this.lstOffersAll = objects;
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


            Offer offer = (Offer) lstOffersAll.get(position);

            holder.txtView.setText(offer.getShopName());
            UtilityFirebase.getShop(offer).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        Shop shop = dataSnapshot.getValue(Shop.class);
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

                    } catch (Exception ex) {
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            return convertView;
        }


        @Override
        public int getCount() {
            return lstOffersAll.size();
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
