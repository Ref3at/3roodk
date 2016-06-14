package com.app3roodk.UI.OffersCards;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app3roodk.R;
import com.app3roodk.Schema.Offer;
import com.app3roodk.UI.DetailActivity.DetailActivity;
import com.app3roodk.UtilityGeneral;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;

public class CardsFragment extends Fragment {

    final static long minutesInMilli = 1000 * 60;
    final static long hoursInMilli = minutesInMilli * 60;
    final static long daysInMilli = hoursInMilli * 24;
    FirebaseRecyclerAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.cards_recycler_view, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        try {
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final List<Address> tempAddresses = UtilityGeneral.getCurrentGovAndCity(getActivity(), UtilityGeneral.getCurrentLonAndLat(getActivity()));
                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Offers").child(tempAddresses.get(0).getAddressLine(2)).child(getActivity().getIntent().getStringExtra("name"));

                                        mAdapter = new FirebaseRecyclerAdapter<Offer, CardHolder>(Offer.class, R.layout.card_item, CardHolder.class, ref) {
                                            @Override
                                            public void populateViewHolder(CardHolder cardHolder, Offer offer, int position) {
                                                cardHolder.offer = offer;
                                                cardHolder.shopName.setText(offer.getShopName());
                                                cardHolder.title.setText(offer.getTitle());
                                                cardHolder.rate.setText(String.valueOf(offer.getAverageRate()));
                                                //  cardHolder.discount.setText(String.format("%.0f", (1 - (Double.parseDouble(offer.getPriceAfter()) / Double.parseDouble(offer.getPriceBefore()))) * 100) + "%");
                                                cardHolder.discount.setText(String.format("%.0f", (1 - (Double.parseDouble(offer.getItems().get(0).getPriceAfter()) / Double.parseDouble(offer.getItems().get(0).getPriceBefore()))) * 100) + "%");
                                                fillTimer(cardHolder, offer);
                                                //     Glide.with(cardHolder.itemView.getContext()).load(offer.getImagePaths().get(0)).into(cardHolder.imgCard);
                                                Glide.with(cardHolder.itemView.getContext()).load(offer.getItems().get(0).getImagePaths().get(0)).into(cardHolder.imgCard);
                                            }
                                        };
                                        recyclerView.setAdapter(mAdapter);
                                    } catch (Exception ex) {
                                        Log.e("CreateShopFragment", ex.getMessage());
                                    }
                                }
                            });
                        } catch (Exception ex) {
                        }
                    }
                }).start();
            } catch (Exception ex) {
            }

        } catch (Exception ex) {
            Log.e("CardsFragment", ex.getMessage());
        }
        return recyclerView;
    }

    private void fillTimer(CardHolder cardHolder, Offer offer) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(offer.getEndTime().substring(0, 4)));
        cal.set(Calendar.MONTH, Integer.parseInt(offer.getEndTime().substring(4, 6)) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(offer.getEndTime().substring(6, 8)));
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(offer.getEndTime().substring(8, 10)));
        cal.set(Calendar.MINUTE, Integer.parseInt(offer.getEndTime().substring(10, 12)));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long timeInMilliseconds = cal.getTime().getTime() - System.currentTimeMillis();
        cardHolder.day.setText(String.valueOf((int) (timeInMilliseconds / daysInMilli)));
        timeInMilliseconds = timeInMilliseconds % daysInMilli;
        cardHolder.hour.setText(String.valueOf((int) (timeInMilliseconds / hoursInMilli)));
        timeInMilliseconds = timeInMilliseconds % hoursInMilli;
        cardHolder.minute.setText(String.valueOf((int) (timeInMilliseconds / minutesInMilli)));
    }

    public void onDestroy() {
        super.onDestroy();
        try {
            mAdapter.cleanup();
        } catch (Exception ex) {
        }
    }
}

class CardHolder extends RecyclerView.ViewHolder {
    public TextView title, rate, distance, shopName, day, hour, minute, discount;
    public ImageView imgCard;
    public Offer offer;

    public CardHolder(View rootView) {
        super(rootView);
        title = (TextView) rootView.findViewById(R.id.card_text);
        discount = (TextView) rootView.findViewById(R.id.card_txt_discount);
        shopName = (TextView) rootView.findViewById(R.id.card_shop_name);
        distance = (TextView) rootView.findViewById(R.id.card_distance);
        rate = (TextView) rootView.findViewById(R.id.card_rate);
        day = (TextView) rootView.findViewById(R.id.card_txt_day);
        hour = (TextView) rootView.findViewById(R.id.card_txt_hour);
        minute = (TextView) rootView.findViewById(R.id.card_txt_minute);
        imgCard = (ImageView) rootView.findViewById(R.id.card_image);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                FirebaseDatabase.getInstance().getReference("Offers").child(offer.getCity()).child(offer.getCategoryName()).child(offer.getObjectId()).child("viewNum").setValue(offer.getViewNum() + 1);
                offer.setViewNum(offer.getViewNum() + 1);
                intent.putExtra("offer", new Gson().toJson(offer));
                v.getContext().startActivity(intent);
            }
        });
    }
}