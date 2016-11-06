package com.app3roodk.UI.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.app3roodk.Schema.OfferMgallat;
import com.app3roodk.UI.DetailActivity.DetailActivity;
import com.app3roodk.UtilityFirebase;
import com.app3roodk.UtilityGeneral;
import com.bumptech.glide.Glide;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;

public class AllHypersFragment extends Fragment {

    public static String SelectedCity;
    public static String SelectedGov;
    SpinKitView progress;
    ExpandableHeightGridView gridView;
    ValueEventListener OfferListener;
    Thread trdCurrentLocationSpinner;
    private SampleAdapter adapter;
    private ArrayList<OfferMgallat> lstOffers;
    private Query qrHyperOffers;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_hypers, container, false);
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
        lstOffers = new ArrayList<>();
        adapter = new SampleAdapter(getActivity(), R.layout.item_hyper, lstOffers);
        gridView.setAdapter(adapter);
        gridView.setExpanded(true);
        clickConfig();
        initListener();
        getOffers();
    }

    private void showMessage(String msg) {
        if (isAdded() && !isDetached() && isVisible())
            Snackbar.make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
    }

    private void initListener() {

        OfferListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lstOffers.clear();
                adapter.notifyDataSetChanged();
                progress.setVisibility(View.GONE);
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                OfferMgallat of;
                DataSnapshot ds;
                while (iterator.hasNext()) {
                    ds = iterator.next();
                    of = ds.getValue(OfferMgallat.class);
                    of.setObjectId(ds.getKey());
                    lstOffers.add(of);
                }
                if (lstOffers.size() == 0)
                    showMessage("لا يوجد عروض لهايبرات فى منطقتك الحاليه");
                adapter.notifyDataSetChanged();
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
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                FirebaseDatabase.getInstance().getReference("OffersHyper").child(lstOffers.get(i).getCity()).child(lstOffers.get(i).getObjectId()).child("viewNum").setValue(lstOffers.get(i).getViewNum() + 1);
                lstOffers.get(i).setViewNum(lstOffers.get(i).getViewNum() + 1);
                intent.putExtra("offer", new Gson().toJson(lstOffers.get(i)));
                intent.putExtra("details", Constants.DETAILS_OFFLINE_HYPERS);
                startActivity(intent);
            }
        });

    }

//    public void changeCities(int i, ArrayList<String> lstCities) {
//        try {
//            if (i == 0)
//                if (!((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER))
//                    Toast.makeText(getActivity().getBaseContext(), "افتح الـ Location او سيتم أخذ آخر مكان مسجل", Toast.LENGTH_SHORT).show();
//
//            if (trdCurrentLocationSpinner != null && trdCurrentLocationSpinner.isAlive())
//                trdCurrentLocationSpinner.interrupt();
//            if (i == 0) {
//                trdCurrentLocationSpinner = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        final String city = UtilityGeneral.getCurrentCityArabic(getActivity());
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (city == null) {
//                                    return;
//                                }
//                                SelectedCity = city;
//                                getOffers();
//                            }
//                        });
//                    }
//                });
//                trdCurrentLocationSpinner.start();
//            } else {
//                SelectedCity = lstCities.get(i);
//                getOffers();
//            }
//
//        } catch (Exception ex) {
//        }
//    }

    private void getOffers() {
        try {
            qrHyperOffers.removeEventListener(OfferListener);
        } catch (Exception ignored) {
        }
        progress.setVisibility(View.VISIBLE);
        qrHyperOffers = UtilityFirebase.getHyperOffers(SelectedCity);
        qrHyperOffers.addValueEventListener(OfferListener);

    }

    @Override
    public void onDestroy() {
        try {
            qrHyperOffers.removeEventListener(OfferListener);
        } catch (Exception ignored) {
        }
        super.onDestroy();
    }

    class SampleAdapter extends ArrayAdapter {

        private LayoutInflater mLayoutInflater;
        private ArrayList<OfferMgallat> lstOffers;


        SampleAdapter(Context context, int textViewResourceId,
                      ArrayList<OfferMgallat> objects) {
            super(context, textViewResourceId);
            this.mLayoutInflater = LayoutInflater.from(context);
            this.lstOffers = objects;
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
            holder.txtView.setText(lstOffers.get(position).getShopName());
            Glide.with(getContext()).load(lstOffers.get(position).getLogo()).into(holder.imgView);
            return convertView;
        }

        @Override
        public int getCount() {
            return lstOffers.size();
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
