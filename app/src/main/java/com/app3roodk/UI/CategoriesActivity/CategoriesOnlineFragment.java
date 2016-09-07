package com.app3roodk.UI.CategoriesActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app3roodk.R;
import com.app3roodk.Schema.ExistOffers;
import com.app3roodk.Schema.User;
import com.app3roodk.UI.OffersCards.CardsActivity;
import com.app3roodk.UtilityFirebase;
import com.app3roodk.UtilityGeneral;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class CategoriesOnlineFragment extends Fragment {

    TextView t1, t2, t3, t4, t5, t6, t7, t8, t9; // for text offers no
    View v1, v2, v3, v4, v5, v6, v7, v8, v9; // for red circle
    Boolean bOfferExists = false;
    ValueEventListener availableOfferListener;
    ValueEventListener activeOffersListener;
    SpinKitView progress;

    DatabaseReference drActiveOffers,drAvailableOffer;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories_online, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        t1 = (TextView) rootView.findViewById(R.id.t1);
        t2 = (TextView) rootView.findViewById(R.id.t2);
        t3 = (TextView) rootView.findViewById(R.id.t3);
        t4 = (TextView) rootView.findViewById(R.id.t4);
        t5 = (TextView) rootView.findViewById(R.id.t5);
        t6 = (TextView) rootView.findViewById(R.id.t6);
        t7 = (TextView) rootView.findViewById(R.id.t7);
        t8 = (TextView) rootView.findViewById(R.id.t8);
        t9 = (TextView) rootView.findViewById(R.id.t9);

        v1 = rootView.findViewById(R.id.v1);
        v2 = rootView.findViewById(R.id.v2);
        v3 = rootView.findViewById(R.id.v3);
        v4 = rootView.findViewById(R.id.v4);
        v5 = rootView.findViewById(R.id.v5);
        v6 = rootView.findViewById(R.id.v6);
        v7 = rootView.findViewById(R.id.v7);
        v8 = rootView.findViewById(R.id.v8);
        v9 = rootView.findViewById(R.id.v9);
        try {
            if (!UtilityGeneral.mGoogleApiClient.isConnected() && !UtilityGeneral.mGoogleApiClient.isConnecting())
                UtilityGeneral.mGoogleApiClient.connect();
        } catch (Exception ex) {
        }
        progress = (SpinKitView) rootView.findViewById(R.id.progress);
        clickConfig(rootView);
        updateUserLocation();
        initListener();
        updateOffersNumber();
        if (UtilityGeneral.isRegisteredUser(getActivity())) {
            UtilityFirebase.updateUserNotificationToken(getActivity(), UtilityGeneral.loadUser(getActivity()).getObjectId(), FirebaseInstanceId.getInstance().getToken());
            UtilityFirebase.getUserShops(getActivity(), UtilityGeneral.loadUser(getActivity()).getObjectId());
            drAvailableOffer.addValueEventListener(availableOfferListener);
        }

    }

    private void updateUserLocation() {
        try {
            User user = UtilityGeneral.loadUser(getActivity());
            LatLng location = UtilityGeneral.getCurrentLonAndLat(getActivity());
            if (location == null) return;
            user.setLat(String.valueOf(location.latitude));
            user.setLon(String.valueOf(location.longitude));
            UtilityGeneral.saveUser(getActivity(), user);
        } catch (Exception ex) {
        }
    }

    private void showMessage(String msg) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
    }

    private void initListener() {
        availableOfferListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) return;
                String yearWeek = UtilityGeneral.getCurrentYearAndWeek();
                User user = UtilityGeneral.loadUser(getActivity());
                if (user.getNumOfOffersAvailable().containsKey(yearWeek))
                    user.getNumOfOffersAvailable().remove(yearWeek);
                user.getNumOfOffersAvailable().put(yearWeek, dataSnapshot.getValue(Integer.class));
                if (UtilityGeneral.isRegisteredUser(getActivity()))
                    UtilityGeneral.saveUser(getActivity(), user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        activeOffersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progress.setVisibility(View.GONE);
                if (dataSnapshot.getValue() == null) {
                    hideViews();
                    bOfferExists = false;
                    showMessage("لا يوجد عروض");
                    return;
                }
                int HomeTools, Accessories, Mobiles, Computers, Shoes, Clothes, SuperMarket, Services, Restaurants;
                HomeTools = Accessories = Mobiles = Computers = Shoes = Clothes = SuperMarket = Services = Restaurants = 0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    try {
                            switch (dataSnapshot1.getValue(ExistOffers.class).getCategory()) {
                                case "Home Tools":
                                    HomeTools++;
                                    break;
                                case "Accessories":
                                    Accessories++;
                                    break;
                                case "Mobiles":
                                    Mobiles++;
                                    break;
                                case "Computers":
                                    Computers++;
                                    break;
                                case "Shoes":
                                    Shoes++;
                                    break;
                                case "Clothes":
                                    Clothes++;
                                    break;
                                case "Super market":
                                    SuperMarket++;
                                    break;
                                case "Services":
                                    Services++;
                                    break;
                                case "Restaurants":
                                    Restaurants++;
                                    break;
                            }
                    } catch (Exception ex) {
                    }
                }
                if (Restaurants > 0) {
                    t1.setText(String.valueOf(Restaurants));
                    t1.setVisibility(View.VISIBLE);
                    v1.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }
                if (HomeTools > 0) {
                    t2.setText(String.valueOf(HomeTools));
                    t2.setVisibility(View.VISIBLE);
                    v2.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }
                if (Accessories > 0) {
                    t3.setText(String.valueOf(Accessories));
                    t3.setVisibility(View.VISIBLE);
                    v3.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }
                if (Mobiles > 0) {
                    t4.setText(String.valueOf(Mobiles));
                    t4.setVisibility(View.VISIBLE);
                    v4.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }
                if (Computers > 0) {
                    t5.setText(String.valueOf(Computers));
                    t5.setVisibility(View.VISIBLE);
                    v5.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }
                if (Shoes > 0) {
                    t6.setText(String.valueOf(Shoes));
                    t6.setVisibility(View.VISIBLE);
                    v6.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }
                if (Clothes > 0) {
                    t7.setText(String.valueOf(Clothes));
                    t7.setVisibility(View.VISIBLE);
                    v7.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }
                if (SuperMarket > 0) {
                    t8.setText(String.valueOf(SuperMarket));
                    t8.setVisibility(View.VISIBLE);
                    v8.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }
                if (Services > 0) {
                    t9.setText(String.valueOf(Services));
                    t9.setVisibility(View.VISIBLE);
                    v9.setVisibility(View.VISIBLE);
                    bOfferExists = true;
                }

                if (!bOfferExists) {
                    showMessage("لا يوجد عروض");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
            drActiveOffers = UtilityFirebase.getActiveExistOffersOnline();
            drAvailableOffer = UtilityFirebase.getUserNoOfAvailableOffers(getActivity(), UtilityGeneral.getCurrentYearAndWeek());

    }

    private void clickConfig(View rootView) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCards(view);
            }
        };
        rootView.findViewById(R.id.imageButton1).setOnClickListener(listener);
        rootView.findViewById(R.id.imageButton2).setOnClickListener(listener);
        rootView.findViewById(R.id.imageButton3).setOnClickListener(listener);
        rootView.findViewById(R.id.imageButton4).setOnClickListener(listener);
        rootView.findViewById(R.id.imageButton5).setOnClickListener(listener);
        rootView.findViewById(R.id.imageButton6).setOnClickListener(listener);
        rootView.findViewById(R.id.imageButton7).setOnClickListener(listener);
        rootView.findViewById(R.id.imageButton8).setOnClickListener(listener);
        rootView.findViewById(R.id.imageButton9).setOnClickListener(listener);

    }

    public void goToCards(View view) {
        int x = view.getId();
        Intent i = new Intent(getActivity(), CardsActivity.class);
        i.putExtra("online",true);
        switch (x) {

            case R.id.imageButton1:

                i.putExtra("title", "مطاعم");
                i.putExtra("name", "Restaurants");
                break;
            case R.id.imageButton2:
                i.putExtra("title", "أدوات منزليه");
                i.putExtra("name", "Home Tools");

                break;
            case R.id.imageButton3:
                i.putExtra("title", "إكسسوار");
                i.putExtra("name", "Accessories");
                break;
            case R.id.imageButton4:
                i.putExtra("title", "موبايل");
                i.putExtra("name", "Mobiles");
                break;
            case R.id.imageButton5:
                i.putExtra("title", "كمبيوتر");
                i.putExtra("name", "Computers");
                break;
            case R.id.imageButton6:
                i.putExtra("title", "أحذية");
                i.putExtra("name", "Shoes");
                break;
            case R.id.imageButton7:
                i.putExtra("title", "ملابس");
                i.putExtra("name", "Clothes");
                break;
            case R.id.imageButton8:
                i.putExtra("name", "Super market");
                i.putExtra("title", "سوبر ماركت");
                break;
            case R.id.imageButton9:
                i.putExtra("name", "Services");
                i.putExtra("title", "خدمات");
                break;
        }
        startActivityForResult(i, 222);
    }

    @Override
    public void onDestroy() {
        try {
            drAvailableOffer.removeEventListener(availableOfferListener);
        } catch (Exception ex) {
        }
        try {
            drActiveOffers.removeEventListener(activeOffersListener);
        } catch (Exception ex) {
        }
        try {
            UtilityGeneral.mGoogleApiClient.disconnect();
        } catch (Exception ex) {
        }
        super.onDestroy();
    }


    private void updateOffersNumber() {

        try {
            drActiveOffers.removeEventListener(activeOffersListener);
        } catch (Exception ex) {
        }
        try {
            progress.setVisibility(View.VISIBLE);
            drActiveOffers.addValueEventListener(activeOffersListener);
        } catch (Exception ex) {
        }
    }

    private void hideViews() {

        v1.setVisibility(View.GONE);
        v2.setVisibility(View.GONE);
        v3.setVisibility(View.GONE);
        v4.setVisibility(View.GONE);
        v5.setVisibility(View.GONE);
        v6.setVisibility(View.GONE);
        v7.setVisibility(View.GONE);
        v8.setVisibility(View.GONE);
        v9.setVisibility(View.GONE);

        t1.setVisibility(View.GONE);
        t2.setVisibility(View.GONE);
        t3.setVisibility(View.GONE);
        t4.setVisibility(View.GONE);
        t5.setVisibility(View.GONE);
        t6.setVisibility(View.GONE);
        t7.setVisibility(View.GONE);
        t8.setVisibility(View.GONE);
        t9.setVisibility(View.GONE);

    }

}
