package com.app3roodk.UI.MainActivity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.app3roodk.Schema.OfferMgallat;
import com.app3roodk.Schema.OffersInterface;
import com.app3roodk.UI.DetailActivity.DetailActivity;
import com.app3roodk.UtilityFirebase;
import com.app3roodk.UtilityGeneral;
import com.bumptech.glide.Glide;
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

public class AllOffersFragment extends Fragment {

    public static String SelectedCity;
    public static String SelectedGov;


    SpinKitView progress;
    ExpandableHeightGridView gridView;
    ValueEventListener hypersOfferListener;
    ValueEventListener[] shopsOfferListeners;
    Thread trdCurrentLocationSpinner;
    String[] categoriesArray;
    private SampleAdapter adapter;
    private ArrayList<OffersInterface> lstAllOffers;
    private Query qrHyperOffers;
    private Query[] qrShopsOffers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_all_offers, container, false);

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

        try {
            qrHyperOffers.removeEventListener(hypersOfferListener);
        } catch (Exception ignored) {
        }
        qrHyperOffers = UtilityFirebase.getHyperOffers(SelectedCity);
        qrHyperOffers.addListenerForSingleValueEvent(hypersOfferListener);

        for (int i = 0; i < 9; i++) {
            try {
                qrShopsOffers[i].removeEventListener(shopsOfferListeners[i]);
            } catch (Exception ignored) {
            }
            qrShopsOffers[i] = UtilityFirebase.getActiveOffers(SelectedCity, categoriesArray[i]);
            qrShopsOffers[i].addListenerForSingleValueEvent(shopsOfferListeners[i]);

        }
    }


    private void initListener() {
        lstAllOffers.clear();

        hypersOfferListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged();
                progress.setVisibility(View.GONE);
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                OfferMgallat of;
                DataSnapshot ds;
                while (iterator.hasNext()) {
                    ds = iterator.next();
                    of = ds.getValue(OfferMgallat.class);
                    of.setObjectId(ds.getKey());
                    lstAllOffers.add(of);
                }
//                if (lstAllOffers.size() == 0)
//                    showMessage("لا يوجد عروض لهايبرات فى منطقتك الحاليه");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progress.setVisibility(View.GONE);
                showMessage("بص على النت كده");
            }
        };

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

                if (lstAllOffers.get(i) instanceof OfferMgallat) {
                    OfferMgallat offerMgallat = (OfferMgallat) lstAllOffers.get(i);
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    //FirebaseDatabase.getInstance().getReference("OffersHyper").child(offerMgallat.getCity()).child(offerMgallat.getObjectId()).child("viewNum").setValue(offerMgallat.getViewNum() + 1);
                    offerMgallat.setViewNum(offerMgallat.getViewNum() + 1);
                    intent.putExtra("offer", new Gson().toJson(lstAllOffers.get(i)));
                    intent.putExtra("details", Constants.DETAILS_OFFLINE_HYPERS);
                    startActivity(intent);
                } else if (lstAllOffers.get(i) instanceof Offer) {
                    Offer offer = (Offer) lstAllOffers.get(i);

                    Intent intent = new Intent(view.getContext(), DetailActivity.class);
                    //  FirebaseDatabase.getInstance().getReference("Offers").child(offer.getCity()).child(offer.getCategoryName()).child(offer.getObjectId()).child("viewNum").setValue(offer.getViewNum() + 1);
                    offer.setViewNum(offer.getViewNum() + 1);
                    intent.putExtra("offer", new Gson().toJson(offer));
                    intent.putExtra("details", Constants.DETAILS_OFFLINE_SHOPS);
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
        private ArrayList<OffersInterface> lstOffersAll;


        SampleAdapter(Context context, int textViewResourceId,
                      ArrayList<OffersInterface> objects) {
            super(context, textViewResourceId);
            this.mLayoutInflater = LayoutInflater.from(context);
            this.lstOffersAll = objects;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView,
                            @NonNull ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {

                convertView = mLayoutInflater.inflate(R.layout.item_hyper, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (lstOffersAll.get(position) instanceof OfferMgallat) {
                OfferMgallat offerMgallat = (OfferMgallat) lstOffersAll.get(position);
                holder.txtView.setText(offerMgallat.getShopName());
                Glide.with(getContext()).load(offerMgallat.getLogo()).into(holder.imgView);

            } else if (lstOffersAll.get(position) instanceof Offer) {
                Offer offer = (Offer) lstOffersAll.get(position);

                holder.txtView.setText(offer.getTitle());
                Glide.with(getContext()).load(offer.getItems().get(0).getImagePaths().get(0)).into(holder.imgView);
            }


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
