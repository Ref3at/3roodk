package com.app3roodk.UI.FavoritesCards;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app3roodk.R;
import com.app3roodk.Schema.Offer;
import com.app3roodk.Schema.TestTable;
import com.app3roodk.UI.DetailActivity.DetailActivity;
import com.app3roodk.UtilityGeneral;

import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class FavoritesFragment extends Fragment {

    ArrayList<Offer> lstOffers;
    ContentAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.cards_recycler_view, container, false);
        lstOffers = new ArrayList<>();
        adapter = new ContentAdapter(lstOffers);
        recyclerView.setAdapter(adapter);
        if (lstOffers.size() > 0) {
            adapter.notifyDataSetChanged();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        new FetchFavOffers(recyclerView).execute();
        return recyclerView;
    }


    public class FetchFavOffers extends AsyncTask<Void, Void, Void> {

        RecyclerView recyclerView;

        public FetchFavOffers(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Cursor cursor = getActivity().getContentResolver().query(TestTable.CONTENT_URI, null, null, null, null);

            lstOffers = (ArrayList<Offer>) TestTable.getRows(cursor, false);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new ContentAdapter(lstOffers);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }


    public static class ContentAdapter extends RecyclerView.Adapter<CardHolder> {
        final static long secondsInMilli = 1000;
        final static long minutesInMilli = secondsInMilli * 60;
        final static long hoursInMilli = minutesInMilli * 60;
        final static long daysInMilli = hoursInMilli * 24;
        ArrayList<Offer> lstOffers;

        public ContentAdapter(ArrayList<Offer> Offers) {
            lstOffers = Offers;
        }

        @Override
        public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CardHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        private void fillTimer(CardHolder cardHolder, int position) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, Integer.parseInt(lstOffers.get(position).getEndTime().substring(0, 4)));
            cal.set(Calendar.MONTH, Integer.parseInt(lstOffers.get(position).getEndTime().substring(4, 6)) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(lstOffers.get(position).getEndTime().substring(6, 8)));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(lstOffers.get(position).getEndTime().substring(8, 10)));
            cal.set(Calendar.MINUTE, Integer.parseInt(lstOffers.get(position).getEndTime().substring(10, 12)));
            cal.set(Calendar.SECOND, 60);
            cal.set(Calendar.MILLISECOND, 0);
            long timeInMilliseconds = cal.getTime().getTime() - System.currentTimeMillis();
            cardHolder.day.setText(String.valueOf((int) (timeInMilliseconds / daysInMilli)));
            timeInMilliseconds = timeInMilliseconds % daysInMilli;
            cardHolder.hour.setText(String.valueOf((int) (timeInMilliseconds / hoursInMilli)));
            timeInMilliseconds = timeInMilliseconds % hoursInMilli;
            cardHolder.minute.setText(String.valueOf((int) (timeInMilliseconds / minutesInMilli)));
            timeInMilliseconds = timeInMilliseconds % minutesInMilli;
            cardHolder.second.setText(String.valueOf((int) (timeInMilliseconds / secondsInMilli)));
            if (cardHolder.day.getText().toString().equals("0")) {
                cardHolder.showSeconds = true;
            } else {
                cardHolder.showSeconds = false;
            }
        }

        @Override
        public void onBindViewHolder(final CardHolder cardHolder, final int position) {

            fillTimer(cardHolder, position);
            cardHolder.offer = lstOffers.get(position);
            cardHolder.shopName.setText(lstOffers.get(position).getShopName());
            cardHolder.title.setText(lstOffers.get(position).getTitle());
            cardHolder.rate.setText(String.valueOf(lstOffers.get(position).getAverageRate()));
            cardHolder.discount.setText(String.format("%.0f", (1 - (Double.parseDouble(lstOffers.get(position).getItems().get(0).getPriceAfter()) / Double.parseDouble(lstOffers.get(position).getItems().get(0).getPriceBefore()))) * 100) + "%");
            fillTimer(cardHolder, position);
            Picasso.with(cardHolder.itemView.getContext()).load(lstOffers.get(position).getItems().get(0).getImagePaths().get(0)).into(cardHolder.imgCard);
            cardHolder.priceBefore.setText(cardHolder.offer.getItems().get(0).getPriceBefore());
            cardHolder.priceAfter.setText(cardHolder.offer.getItems().get(0).getPriceAfter());
            try {
                cardHolder.distance.setText(String.valueOf(UtilityGeneral.calculateDistanceInKM(
                        Double.parseDouble(lstOffers.get(position).getLat()),
                        Double.parseDouble(lstOffers.get(position).getLon()),
                        UtilityGeneral.getCurrentLonAndLat(cardHolder.itemView.getContext()).latitude,
                        UtilityGeneral.getCurrentLonAndLat(cardHolder.itemView.getContext()).longitude)) + "km");
                cardHolder.distance.setVisibility(View.VISIBLE);
            } catch (Exception ex) {
                cardHolder.distance.setVisibility(View.INVISIBLE);
            }

            if (cardHolder.timer != null)
                cardHolder.timer.cancel();
            cardHolder.timer = new CountDownTimer(180000, 500) {
                @Override
                public void onTick(long l) {
                    try {
                        fillTimer(cardHolder, position);
                        if (!cardHolder.showSeconds) {
                            cardHolder.dots3.setVisibility(View.GONE);
                            cardHolder.lytSeconds.setVisibility(View.GONE);
                            cardHolder.lytDays.setVisibility(View.VISIBLE);
                            if (cardHolder.dots2.getVisibility() == View.INVISIBLE) {
                                cardHolder.dots1.setVisibility(View.VISIBLE);
                                cardHolder.dots2.setVisibility(View.VISIBLE);
                            } else {
                                cardHolder.dots1.setVisibility(View.INVISIBLE);
                                cardHolder.dots2.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            cardHolder.dots1.setVisibility(View.GONE);
                            cardHolder.lytDays.setVisibility(View.GONE);
                            cardHolder.lytSeconds.setVisibility(View.VISIBLE);
                            if (cardHolder.dots2.getVisibility() == View.INVISIBLE) {
                                cardHolder.dots3.setVisibility(View.VISIBLE);
                                cardHolder.dots2.setVisibility(View.VISIBLE);
                            } else {
                                cardHolder.dots3.setVisibility(View.INVISIBLE);
                                cardHolder.dots2.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                    catch ( Exception ex)
                    {
                        cardHolder.timer.cancel();
                    }
                }

                @Override
                public void onFinish() {

                }
            };
            cardHolder.timer.start();
        }

        @Override
        public int getItemCount() {
            return lstOffers.size();
        }
    }


}

class CardHolder extends RecyclerView.ViewHolder {
    public TextView title, rate, distance, shopName, day, hour, minute, second, discount, priceBefore, priceAfter, dots1, dots2, dots3;
    public ImageView imgCard;
    public LinearLayout lytSeconds, lytDays;
    public boolean showSeconds;
    public CountDownTimer timer;
    public Offer offer;


//    ImageButton btnFavorite, btnShare;

    public CardHolder(LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.card_item_offline, parent, false));
        init(itemView);
    }

    public void init(View rootView) {
        title = (TextView) rootView.findViewById(R.id.card_text);
        discount = (TextView) rootView.findViewById(R.id.card_txt_discount);
        shopName = (TextView) rootView.findViewById(R.id.card_shop_name);
        distance = (TextView) rootView.findViewById(R.id.card_distance);
        priceAfter = (TextView) rootView.findViewById(R.id.card_price_after);
        priceBefore = (TextView) rootView.findViewById(R.id.card_price_before);
        rate = (TextView) rootView.findViewById(R.id.card_rate);
        day = (TextView) rootView.findViewById(R.id.card_txt_day);
        hour = (TextView) rootView.findViewById(R.id.card_txt_hour);
        second = (TextView) rootView.findViewById(R.id.card_txt_second);
        dots1 = (TextView) rootView.findViewById(R.id.txt_dots_1);
        dots2 = (TextView) rootView.findViewById(R.id.txt_dots_2);
        dots3 = (TextView) rootView.findViewById(R.id.txt_dots_3);
        minute = (TextView) rootView.findViewById(R.id.card_txt_minute);
        imgCard = (ImageView) rootView.findViewById(R.id.card_image);
        lytDays = (LinearLayout) rootView.findViewById(R.id.lytDay);
        lytSeconds = (LinearLayout) rootView.findViewById(R.id.lytSeconds);
        //btnFavorite = (ImageButton) rootView.findViewById(R.id.favorite_button);
        //btnShare = (ImageButton) rootView.findViewById(R.id.share_button2);

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

//        btnFavorite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "i will add", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        btnShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "i will share", Toast.LENGTH_SHORT).show();
//
//            }
//        });
    }
}