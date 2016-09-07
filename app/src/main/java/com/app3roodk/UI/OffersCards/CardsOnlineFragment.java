package com.app3roodk.UI.OffersCards;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app3roodk.Constants;
import com.app3roodk.R;
import com.app3roodk.Schema.Offer;
import com.app3roodk.UI.DetailActivity.DetailActivity;
import com.app3roodk.UtilityFirebase;
import com.app3roodk.UtilityGeneral;
import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;

public class CardsOnlineFragment extends Fragment {

    public int fragmentType;
    ArrayList<Offer> lstOffers;
    ContentAdapter adapter;
    ValueEventListener postListener;
    ImageView imgNoOffer;
    SpinKitView progress;
    DatabaseReference drActiveOffersOnline;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.cards_recycler_view, container, false);

        recyclerView.clearAnimation();

        progress = (SpinKitView) getActivity().findViewById(R.id.progress);
        imgNoOffer = (ImageView) getActivity().findViewById(R.id.img_no_offer);
        if (!UtilityGeneral.isOnline(getActivity()))
            lstOffers = UtilityGeneral.loadOffers(getActivity(), getActivity().getIntent().getStringExtra("name"));
        else {
            lstOffers = new ArrayList<>();
            progress.setVisibility(View.VISIBLE);
        }
        adapter = new ContentAdapter(lstOffers);
        recyclerView.setAdapter(adapter);
        if (lstOffers.size() > 0) {
            sortOffers();
            adapter.notifyDataSetChanged();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        initListener();
        fetchOffers();
        if (!UtilityGeneral.isOnline(getActivity())) {
            progress.setVisibility(View.GONE);
            showMessage("انت غير متصل بالإنترنت");
        }
        ((Callback) getActivity()).onItemSelected(recyclerView);

        return recyclerView;
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {

        void onItemSelected(RecyclerView recyclerView);

    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface CallbackSnackBehavior {

        void onSnackAppear();

    }


    private void fetchOffers() {
        drActiveOffersOnline = UtilityFirebase.getActiveOffersOnline(getActivity().getIntent().getStringExtra("name"));
        drActiveOffersOnline.addValueEventListener(postListener);
    }

    private void initListener() {
        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lstOffers.clear();
                adapter.notifyDataSetChanged();
                progress.setVisibility(View.INVISIBLE);
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                Offer of;
                DataSnapshot ds;
                while (iterator.hasNext()) {
                    ds= iterator.next();
                    of = ds.getValue(Offer.class);
                    of.setObjectId(ds.getKey());
                        lstOffers.add(of);
                }
                try {
                    UtilityGeneral.saveOffers(getActivity(), getActivity().getIntent().getStringExtra("name"), lstOffers);
                } catch (Exception ex) {
                }
                sortOffers();
                if (lstOffers.size() == 0)
                    imgNoOffer.setVisibility(View.VISIBLE);
                else
                    imgNoOffer.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                imgNoOffer.setVisibility(View.VISIBLE);
                showMessage("بص على النت كده");
            }
        };
    }

    private void sortOffers() {
        if (lstOffers.size() == 0) return;

        if (fragmentType == Constants.FRAGMENT_BEST_SALE)
            UtilityGeneral.sortOffersByDiscount(lstOffers);
        else if (fragmentType == Constants.FRAGMENT_MOST_VIEWED)
            UtilityGeneral.sortOffersByViews(lstOffers);
        else if (fragmentType == Constants.FRAGMENT_NEWEST)
            UtilityGeneral.sortOffersByNewest(lstOffers);
        adapter.notifyDataSetChanged();
    }

    public class ContentAdapter extends RecyclerView.Adapter<CardHolder> {
        ArrayList<Offer> lstOffers;

        public ContentAdapter(ArrayList<Offer> Offers) {
            lstOffers = Offers;
        }

        @Override
        public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CardHolder(LayoutInflater.from(parent.getContext()), parent);
        }


        @Override
        public void onBindViewHolder(final CardHolder cardHolder, final int position) {

            cardHolder.offer = lstOffers.get(position);
            cardHolder.shopName.setText(lstOffers.get(position).getShopName());
            cardHolder.title.setText(lstOffers.get(position).getTitle());
            cardHolder.rate.setText(String.valueOf(lstOffers.get(position).getAverageRate()));
            cardHolder.discount.setText(String.format("%.0f", (1 - (Double.parseDouble(lstOffers.get(position).getItems().get(0).getPriceAfter()) / Double.parseDouble(lstOffers.get(position).getItems().get(0).getPriceBefore()))) * 100) + "%");


            Glide.with(cardHolder.itemView.getContext()).load(lstOffers.get(position).getItems().get(0).getImagePaths().get(0)).into(cardHolder.imgCard);
            // Picasso.with(cardHolder.itemView.getContext()).load(lstOffers.get(position).getItems().get(0).getImagePaths().get(0)).into(cardHolder.imgCard);


            cardHolder.priceBefore.setText(cardHolder.offer.getItems().get(0).getPriceBefore());
            cardHolder.priceAfter.setText(cardHolder.offer.getItems().get(0).getPriceAfter());
        }

        @Override
        public int getItemCount() {
            return lstOffers.size();
        }
    }

    private void showMessage(String msg) {
        ((CallbackSnackBehavior) getActivity()).onSnackAppear();
        Snackbar.make(getActivity().findViewById(R.id.fab), msg, Snackbar.LENGTH_LONG).show();
    }

    public void onDestroy() {
        try {
            drActiveOffersOnline.removeEventListener(postListener);
        } catch (Exception ex) {
        }
        super.onDestroy();
    }


    class CardHolder extends RecyclerView.ViewHolder {
        public TextView title, rate, shopName, discount, priceBefore, priceAfter;
        public ImageView imgCard;
        public Button btnBuy;
        public Offer offer;

//    ImageButton btnFavorite, btnShare;

        public CardHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.card_item_online, parent, false));
            init(itemView);
        }

        public void init(View rootView) {
            title = (TextView) rootView.findViewById(R.id.card_text);
            discount = (TextView) rootView.findViewById(R.id.card_txt_discount);
            shopName = (TextView) rootView.findViewById(R.id.card_shop_name);
            priceAfter = (TextView) rootView.findViewById(R.id.card_price_after);
            priceBefore = (TextView) rootView.findViewById(R.id.card_price_before);
            rate = (TextView) rootView.findViewById(R.id.card_rate);
            imgCard = (ImageView) rootView.findViewById(R.id.card_image);
            btnBuy = (Button) rootView.findViewById(R.id.btnBuy);


            rootView.findViewById(R.id.btnBuy).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(offer.getCity())));
                }
            });
            //btnFavorite = (ImageButton) rootView.findViewById(R.id.favorite_button);
            //btnShare = (ImageButton) rootView.findViewById(R.id.share_button2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetailActivity.class);
                    FirebaseDatabase.getInstance().getReference("OffersOnline").child(offer.getCategoryName()).child(offer.getObjectId()).child("viewNum").setValue(offer.getViewNum() + 1);
                    offer.setViewNum(offer.getViewNum() + 1);
                    intent.putExtra("offer", new Gson().toJson(offer));
                    intent.putExtra("online",true);
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
}