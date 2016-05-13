package com.app3roodk.UI.OffersCards;


import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.app3roodk.Back4App.UtilityRestApi;
import com.app3roodk.ObjectConverter;
import com.app3roodk.R;
import com.app3roodk.Schema.Offer;
import com.app3roodk.UI.DetailActivity.DetailActivity;
import com.app3roodk.UtilityGeneral;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

/**
 * Provides UI for the view with Cards.
 */
public class CardsFragment extends Fragment {
    static public ArrayList<Offer> lstOffers = new ArrayList<>();
    static public int mode;
    ContentAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.cards_recycler_view, container, false);
        try {
            adapter = new ContentAdapter();
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            lstOffers.clear();
            adapter.notifyDataSetChanged();
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            LatLng latLng = UtilityGeneral.getCurrentLonAndLat(getActivity());
            if (latLng == null) latLng = UtilityGeneral.loadLatLong(getActivity());
            UtilityRestApi.getActiveCategoryOffersByLonAndLat(getContext(), latLng.latitude, latLng.longitude, 0.5, getActivity().getIntent().getStringExtra("name"), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    try {
                        Toast.makeText(getActivity(), responseString, Toast.LENGTH_LONG).show();
                    } catch (Exception ex) {
                        try {
                            Log.e("CardsFragment", ex.getMessage());
                        }
                        catch (Exception exs){}
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        if (isFragmentUINotActive())
                            return;
                        lstOffers.clear();
                        JSONArray result = new JSONObject(responseString).getJSONArray("results");
                        for (int i = 0; i < result.length(); i++) {
                            lstOffers.add(ObjectConverter.convertJsonToOffer(result.getJSONObject(i)));
                        }
                        UtilityGeneral.sortOffersByViews(lstOffers);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            Log.e("CardsFragment", ex.getMessage());
        }
        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.card_item, parent, false));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("objectId", (String) itemView.getTag());
                    context.startActivity(intent);
                }
            });

        }
    }

    public boolean isFragmentUINotActive() {
        return !(isAdded() && !isDetached() && !isRemoving());
    }

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of Card in RecyclerView.
        final static long minutesInMilli = 1000 * 60;
        final static long hoursInMilli = minutesInMilli * 60;
        final static long daysInMilli = hoursInMilli * 24;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        private void fillTimer(CardHolder cardHolder, int position) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, Integer.parseInt(lstOffers.get(position).getEndTime().substring(0, 4)));
            cal.set(Calendar.MONTH, Integer.parseInt(lstOffers.get(position).getEndTime().substring(4, 6)) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(lstOffers.get(position).getEndTime().substring(6, 8)));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long timeInMilliseconds = cal.getTime().getTime() - System.currentTimeMillis();
            cardHolder.day.setText(String.valueOf((int) (timeInMilliseconds / daysInMilli)));
            timeInMilliseconds = timeInMilliseconds % daysInMilli;
            cardHolder.hour.setText(String.valueOf((int) (timeInMilliseconds / hoursInMilli)));
            timeInMilliseconds = timeInMilliseconds % hoursInMilli;
            cardHolder.minute.setText(String.valueOf((int) (timeInMilliseconds / minutesInMilli)));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.itemView.setTag(lstOffers.get(position).getObjectId());
            CardHolder cardHolder = new CardHolder(holder.itemView);
            cardHolder.shopName.setText(lstOffers.get(position).getShopName());
            cardHolder.title.setText(lstOffers.get(position).getTitle());
            cardHolder.rate.setText(String.valueOf(lstOffers.get(position).getAverageRate()));
            cardHolder.discount.setText(String.format("%.0f", (1 - (Double.parseDouble(lstOffers.get(position).getPriceAfter()) / Double.parseDouble(lstOffers.get(position).getPriceBefore()))) * 100) +"%");
            fillTimer(cardHolder, position);
            Picasso.with(holder.itemView.getContext()).load(lstOffers.get(position).getImagePaths().get(0)).into(cardHolder.imgCard);
            // no-op
        }

        @Override
        public int getItemCount() {
            return lstOffers.size();
        }
    }
}

class CardHolder {
    public TextView title, rate, distance, shopName, day, hour, minute, discount;
    public ImageView imgCard;

    CardHolder(View rootView) {
        title = (TextView) rootView.findViewById(R.id.card_text);
        discount = (TextView) rootView.findViewById(R.id.card_txt_discount);
        shopName = (TextView) rootView.findViewById(R.id.card_shop_name);
        distance = (TextView) rootView.findViewById(R.id.card_distance);
        rate = (TextView) rootView.findViewById(R.id.card_rate);
        day = (TextView) rootView.findViewById(R.id.card_txt_day);
        hour = (TextView) rootView.findViewById(R.id.card_txt_hour);
        minute = (TextView) rootView.findViewById(R.id.card_txt_minute);
        imgCard = (ImageView) rootView.findViewById(R.id.card_image);
    }
}
