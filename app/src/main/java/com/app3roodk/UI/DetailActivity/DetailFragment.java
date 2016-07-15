package com.app3roodk.UI.DetailActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.app3roodk.NotificationServices.UtilityCloudMessaging;
import com.app3roodk.ObjectConverter;
import com.app3roodk.R;
import com.app3roodk.Schema.Comments;
import com.app3roodk.Schema.Item;
import com.app3roodk.Schema.Offer;
import com.app3roodk.Schema.Shop;
import com.app3roodk.Schema.TestTable;
import com.app3roodk.UI.FullScreenImage.FullScreenImageSlider;
import com.app3roodk.UI.ImagesSliders.CustomImagesSlider;
import com.app3roodk.UI.Shop.ViewShopActivity;
import com.app3roodk.UtilityFirebase;
import com.app3roodk.UtilityGeneral;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class DetailFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    //region Member Variables
    final static long minutesInMilli = 1000 * 60;
    final static long hoursInMilli = minutesInMilli * 60;
    final static long daysInMilli = hoursInMilli * 24;

    ImageButton btnFavorite, btnShare;
    Button btnShopWay, btnComment;
    TextView txtViews, txtSale, txtPriceBefore, txtPriceAfter, txtDay, txtHour, txtMinute, txtDescription,
            txtShopName, txtWorkTime, txtAddress, txtMobile, txtRate;
    EditText edtxtComment;
    ExpandableHeightListView lsvComments;
    RatingBar ratebar;
    SliderLayout mSlider;

    Calendar cal;

    long timeInMilliseconds;

    Thread timer;

    CommentsAdapter adapter;
    ArrayList<Comments> lstComments;

    Offer offer;
    Shop shop;

    ValueEventListener postListener, offerListener;
    //endregion

    Runnable updateTimerThread = new Runnable() {
        public void run() {
            while (true) {
                timeInMilliseconds = cal.getTime().getTime() - System.currentTimeMillis();
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtDay.setText(String.valueOf((int) (timeInMilliseconds / daysInMilli)));
                            timeInMilliseconds = timeInMilliseconds % daysInMilli;
                            txtHour.setText(String.valueOf((int) (timeInMilliseconds / hoursInMilli)));
                            timeInMilliseconds = timeInMilliseconds % hoursInMilli;
                            txtMinute.setText(String.valueOf((int) (timeInMilliseconds / minutesInMilli)));
                        }
                    });
                    Thread.sleep(30000);
                } catch (Exception ex) {
                    break;
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        init(rootView);
        fillViews();
        initSlider(offer.getItems(), rootView);
        validateUser();
        makeFirebaseReferences();
        clickConfig();
        new FetchFromDB(btnFavorite).execute();
        return rootView;
    }

    private void validateUser() {
        if (!UtilityGeneral.isRegisteredUser(getActivity())) {
            ratebar.setEnabled(false);
            btnComment.setVisibility(View.GONE);
            edtxtComment.setText("سجل الأول علشان تعرف تعمل تعليق!");
            edtxtComment.setEnabled(false);
        } else {
            ratebar.setEnabled(true);
            btnComment.setVisibility(View.VISIBLE);
            edtxtComment.setText("");
            edtxtComment.setEnabled(true);
        }
    }

    private void fillViews() {
        try {
            txtDescription.setText(offer.getDesc());
            txtShopName.setText(offer.getShopName());
            txtViews.setText(String.valueOf(offer.getViewNum()));
            txtRate.setText(String.valueOf(String.valueOf(offer.getAverageRate())));
            if (offer.getShopInfoForFavoeites() != null) {
                HashMap<String, String> shopInfoHashmap = new HashMap<String, String>();
                shopInfoHashmap = ObjectConverter.fromStringToHashmapUsersRates(offer.getShopInfoForFavoeites());
                txtAddress.setText(shopInfoHashmap.get("shopAddress"));
                txtWorkTime.setText(shopInfoHashmap.get("shopWorkingTime"));
                txtMobile.setText(shopInfoHashmap.get("shopContacts"));
            }
            if (offer.getUsersRates().containsKey(UtilityGeneral.loadUser(getActivity()).getObjectId())) {
                ratebar.setRating(Float.parseFloat(offer.getUsersRates().get(UtilityGeneral.loadUser(getActivity()).getObjectId())));
            }
        } catch (Exception ex) {
        }
    }

    FirebaseAuth auth = FirebaseAuth.getInstance();

    private void clickConfig() {
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtxtComment.getText().toString().isEmpty()) {
                    showMessage("اكتب التعليق من فضلك اولاً");
                    return;
                }
                final Comments comment = new Comments();
                if (offer.getUserId().equals(UtilityGeneral.loadUser(getActivity()).getObjectId())) {
                    comment.setUserName(offer.getShopName());
                    comment.setPhotoUrl(UtilityGeneral.loadShop(getActivity(), offer.getShopId()).getLogoId());
                } else {
                    assert auth.getCurrentUser().getPhotoUrl() != null;
                    if (auth.getCurrentUser() != null && auth.getCurrentUser().getPhotoUrl() != null) {
                        comment.setPhotoUrl(auth.getCurrentUser().getPhotoUrl().toString());
                    }
                    comment.setUserName(UtilityGeneral.loadUser(getActivity()).getName());
                }
                comment.setUserId(UtilityGeneral.loadUser(getActivity()).getObjectId());
                comment.setOfferId(offer.getObjectId());
                comment.setCommentText(edtxtComment.getText().toString());

                // hide keyboard
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                // end hide keyboard
                UtilityFirebase.addNewComment(offer, comment, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            showMessage("حصل مشكله شوف النت ");
                            Log.e("DetailFragment", "Add New Comment : " + databaseError.getMessage());
                        } else {
                            if (!(offer.getUserNotificationToken() == null || offer.getUserNotificationToken().isEmpty()
                                    || offer.getShopName() == comment.getUserName())) {
                                HashMap<String, String> mapOffer = new HashMap<>();
                                mapOffer.put("offer", new Gson().toJson(offer));
                                UtilityCloudMessaging.sendNotification(getActivity(), offer.getUserNotificationToken(),
                                        UtilityCloudMessaging.COMMENT_TITLE, comment.getUserName() + UtilityCloudMessaging.COMMENT_BODY,
                                        offer.getObjectId(), mapOffer, "OPEN_ACTIVITY_1", new TextHttpResponseHandler() {
                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                                Log.e("Send Notification err", responseString);
                                            }

                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                                Log.e("Send Notification", responseString);
                                            }
                                        });
                            }
                            edtxtComment.setText("");
                        }
                    }
                });
            }
        });
        ratebar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (!fromUser) return;
                String userId = UtilityGeneral.loadUser(getActivity()).getObjectId();
                if (offer.getUsersRates().containsKey(userId)) {
                    offer.getUsersRates().remove(userId);
                    offer.getUsersRates().put(userId, String.valueOf(rating));

                } else {
                    offer.getUsersRates().put(userId, String.valueOf(rating));
                }

                HashMap<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/usersRates/" + userId + "", String.valueOf(rating));
                if (UtilityGeneral.isTotalAndAverageRatingChanged(offer)) {
                    childUpdates.put("/totalRate", offer.getTotalRate());
                    childUpdates.put("/averageRate", offer.getAverageRate());
                }
                UtilityFirebase.updateOffer(offer, childUpdates);
                txtRate.setText(String.valueOf(offer.getAverageRate()));
            }
        });
        btnShopWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(UtilityGeneral.DrawPathToCertainShop(
                        getContext(), UtilityGeneral.getCurrentLonAndLat(getContext()),
                        new LatLng(Double.parseDouble(offer.getLat()), Double.parseDouble(offer.getLon()))));
            }
        });

        txtShopName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shop != null) {
                    Intent i = new Intent(getActivity(), ViewShopActivity.class);
                    i.putExtra("shop", new Gson().toJson(shop));
                    startActivity(i);
                }
            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (offer != null) {
                    // check if offer is in favorites or not

                    new AsyncTask<Void, Void, Integer>() {

                        @Override
                        protected Integer doInBackground(Void... params) {

                            Cursor cursor = getActivity().getContentResolver().query(TestTable.CONTENT_URI, null
                                    , TestTable.FIELD_OBJECTID + " = ?" // selection
                                    , new String[]{offer.getObjectId()}

                                    , null);
                            int numRows = 0;
                            if (cursor != null) {
                                numRows = cursor.getCount();
                                cursor.close();
                            }
                            return numRows;
                        }

                        @Override
                        protected void onPostExecute(Integer numRows) {
                            // if it is in favorites
                            if (numRows >= 1) {
                                // delete from favorites
                                new AsyncTask<Void, Void, Integer>() {
                                    @Override
                                    protected Integer doInBackground(Void... params) {

                                    /*    // delete poster form storage
                                        if (mMovie.poster_Path != null) {
                                            new File(mMovie.poster_Path.toString()).delete();
                                        } */
                                        return getActivity().getContentResolver().delete(
                                                TestTable.CONTENT_URI,
                                                TestTable.FIELD_OBJECTID + " = ?",
                                                new String[]{offer.getObjectId()}
                                        );
                                    }

                                    @Override
                                    protected void onPostExecute(Integer rowsDeleted) {
                                        btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
                                        // if (mToast != null) {
                                        //    mToast.cancel();
                                        // }
                                        showMessage("تم الإزالة من العروض المفضلة");

                                    }
                                }.execute();
                            }

                            // if it is not in favorites
                            else {
                                // add to favorites
                                new AsyncTask<Void, Void, Void>() {
                                    String poster_loc;
                                    String backdroploc;

                                    HashMap<String, String> shopInfoHashmap = new HashMap<String, String>();


                                    @Override
                                    protected void onPreExecute() {
                                        super.onPreExecute();
                                        if (shop != null) {
                                            shopInfoHashmap.put("shopAddress", shop.getAddress());
                                            shopInfoHashmap.put("shopWorkingTime", shop.getWorkingTime());
                                            shopInfoHashmap.put("shopContacts", shop.getContacts());

                                            offer.setShopInfoForFavoeites(ObjectConverter.fromHashmapToStringUsersRates(shopInfoHashmap));
                                        }


                                        //*************************************************************************************************
                                        //*** hna i will save the poster
                                        //     poster_loc = downloadPoster();
                                        //    backdroploc = downloadBackdrop();

                                    }

                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        //*************************************************************************************************
                                        //*** hna i will save the poster

                                        //  mMovie.setPoster_Path(poster_loc);
                                        //*************************************************************************************************
                                        //*** hna i will save the backdrop
                                        //   mMovie.setBackdrop_path(backdroploc);


                                        getActivity().getContentResolver().insert(TestTable.CONTENT_URI, TestTable.getContentValues(offer, false));
                                        return null;
                                    }


                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);
                                        btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                                        //  checked = true;
                                        showMessage("تم الإضافة إلي العروض المفضلة");

                                    }
                                }.execute();
                            }
                        }
                    }.execute();
                }


            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage("I'll share");

            }
        });
    }

    private void makeFirebaseReferences() {
        try {
            UtilityFirebase.getShop(offer).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        shop = dataSnapshot.getValue(Shop.class);
                        txtAddress.setText(shop.getAddress());
                        txtWorkTime.setText(shop.getWorkingTime());
                        txtMobile.setText(shop.getContacts());
                        txtShopName.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
                    } catch (Exception ex) {
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            UtilityFirebase.getComments(offer).addValueEventListener(postListener);
            UtilityFirebase.getOffer(offer).addValueEventListener(offerListener);
            UtilityFirebase.updateOfferUserNotificationToken(offer);
            if (UtilityGeneral.isTotalAndAverageRatingChanged(offer)) {
                HashMap<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/totalRate", offer.getTotalRate());
                childUpdates.put("/averageRate", offer.getAverageRate());
                UtilityFirebase.updateOffer(offer, childUpdates);
                txtRate.setText(String.valueOf(offer.getAverageRate()));
            }
        } catch (Exception ex) {
        }
    }

    private void init(View rootView) {
        try {
            mSlider = (SliderLayout) rootView.findViewById(R.id.imgOffer);
            btnFavorite = (ImageButton) rootView.findViewById(R.id.btnFavorite);
            btnShare = (ImageButton) rootView.findViewById(R.id.btnShare);
            btnShopWay = (Button) rootView.findViewById(R.id.btnShopWay);
            btnComment = (Button) rootView.findViewById(R.id.btnComment);
            txtViews = (TextView) rootView.findViewById(R.id.txtViews);
            txtSale = (TextView) rootView.findViewById(R.id.txtSale);
            txtPriceBefore = (TextView) rootView.findViewById(R.id.txtPriceBefore);
            txtPriceAfter = (TextView) rootView.findViewById(R.id.txtPriceAfter);
            txtDay = (TextView) rootView.findViewById(R.id.txtDay);
            txtHour = (TextView) rootView.findViewById(R.id.txtHour);
            txtMinute = (TextView) rootView.findViewById(R.id.txtMinute);
            txtDescription = (TextView) rootView.findViewById(R.id.txtDesc);
            txtShopName = (TextView) rootView.findViewById(R.id.txtShopName);
            txtWorkTime = (TextView) rootView.findViewById(R.id.txtWorkTime);
            txtAddress = (TextView) rootView.findViewById(R.id.txtAddress);
            txtMobile = (TextView) rootView.findViewById(R.id.txtMobile);
            txtRate = (TextView) rootView.findViewById(R.id.txtRateNumber);
            edtxtComment = (EditText) rootView.findViewById(R.id.edtxtComment);
            ratebar = (RatingBar) rootView.findViewById(R.id.ratingbar);
            lsvComments = (ExpandableHeightListView) rootView.findViewById(R.id.lsvComments);
            lstComments = new ArrayList<>();
            adapter = new CommentsAdapter(getActivity(), R.layout.comment_item, lstComments);
            lsvComments.setAdapter(adapter);
            lsvComments.setExpanded(true);
            offer = new Gson().fromJson(getActivity().getIntent().getStringExtra("offer"), Offer.class);
            cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, Integer.parseInt(offer.getEndTime().substring(0, 4)));
            cal.set(Calendar.MONTH, Integer.parseInt(offer.getEndTime().substring(4, 6)) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(offer.getEndTime().substring(6, 8)));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(offer.getEndTime().substring(8, 10)));
            cal.set(Calendar.MINUTE, Integer.parseInt(offer.getEndTime().substring(10, 12)));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    lstComments.clear();
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext())
                        lstComments.add(iterator.next().getValue(Comments.class));
                    UtilityGeneral.sortCommentsByTime(lstComments);
                    adapter.notifyDataSetChanged();
//                    UtilityGeneral.setListViewHeightBasedOnChildren(lsvComments);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            offerListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    offer = dataSnapshot.getValue(Offer.class);
                    fillViews();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
        } catch (Exception ex) {
            try {
                getActivity().finish();
            } catch (Exception exx) {
            }
        }
    }

    private void initSlider(List<Item> lstItems, View rootView) {

        for (int i = 0; i < lstItems.size(); i++) {

            for (int j = 0; j < lstItems.get(i).getImagePaths().size(); j++) {

                CustomImagesSlider customImagesSlider = new CustomImagesSlider(getActivity());
                customImagesSlider.setOffer_price_before(offer.getItems().get(i).getPriceBefore());
                customImagesSlider.setOffer_price_after(offer.getItems().get(i).getPriceAfter());
                customImagesSlider.setOffer_sale_perc(String.format("%.0f", (1 - (Double.parseDouble(offer.getItems().get(i).getPriceAfter())
                        / Double.parseDouble(offer.getItems().get(i).getPriceBefore()))) * 100) + "%");

                customImagesSlider
                        .description("حصرى على عروضك")
                        .image(lstItems.get(i).getImagePaths().get(j))
                        .setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
                        .setOnSliderClickListener(this);
                mSlider.addSlider(customImagesSlider);
            }
        }

        if (lstItems.size() == 1 && lstItems.get(0).getImagePaths().size() == 1) {
            mSlider.stopAutoCycle();
            mSlider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
        } else {
            mSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOut);
            mSlider.setCustomIndicator((PagerIndicator) rootView.findViewById(R.id.custom_indicator));
            mSlider.setCustomAnimation(new DescriptionAnimation());
            mSlider.setDuration(5000);
        }
        mSlider.addOnPageChangeListener(this);
    }

    @Override
    public void onStop() {
        mSlider.stopAutoCycle();
        if (timer.isAlive())
            timer.interrupt();
        super.onStop();
    }

    @Override
    public void onResume() {
        try {
            if (offer.getItems().size() == 1 && offer.getItems().get(0).getImagePaths().size() == 1) {
                mSlider.stopAutoCycle();
            } else {
                mSlider.startAutoCycle();
            }
        } catch (Exception ex) {
        }
        ;
        timer = new Thread(updateTimerThread);
        timer.start();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            UtilityFirebase.getComments(offer).removeEventListener(postListener);
            UtilityFirebase.getOffer(offer).removeEventListener(offerListener);
        } catch (Exception ex) {
        }
    }

    private void showMessage(String msg) {
        Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Intent intent = new Intent(getActivity(), FullScreenImageSlider.class);
        ArrayList<String> lstImagePaths = new ArrayList<>();
        for (int i = 0; i < offer.getItems().size(); i++) {
            for (int j = 0; j < offer.getItems().get(i).getImagePaths().size(); j++) {
                lstImagePaths.add(offer.getItems().get(i).getImagePaths().get(j));
            }
        }

        ArrayList<String> imgs = new ArrayList<>();
        for (int i = mSlider.getCurrentPosition(); i < lstImagePaths.size(); i++)
            imgs.add(lstImagePaths.get(i));
        for (int i = 0; i < mSlider.getCurrentPosition(); i++)
            imgs.add(lstImagePaths.get(i));
            intent.putStringArrayListExtra("IMAGES", imgs);
            startActivity(intent);
        }

        public class FetchFromDB extends AsyncTask<ImageButton, Void, ImageButton> {
            int numRows;
            ImageButton ItemFav;

            public FetchFromDB(ImageButton btn) {
                this.ItemFav = btn;

            }

            @Override
            protected ImageButton doInBackground(ImageButton... params) {
                if (offer.getObjectId() != null) {
                    if (getActivity() != null) {
                        Cursor cursor = getActivity().getContentResolver().query(TestTable.CONTENT_URI, null
                                , TestTable.FIELD_OBJECTID + " = ?" // selection
                                , new String[]{offer.getObjectId()}
                                , null);

                        assert cursor != null;
                        if (cursor != null) {
                            this.numRows = cursor.getCount();

                            cursor.close();
                        }
                    }
                }  //testRows = Fav_movieTable.getRows(cursor, false);
                return null;
            }

            @Override
            protected void onPostExecute(ImageButton menuItem) {
                super.onPostExecute(menuItem);


                ItemFav.setImageResource(numRows >= 1 ?
                        android.R.drawable.btn_star_big_on :
                        android.R.drawable.star_big_off);
            }
        }

    }

    class CommentsAdapter extends ArrayAdapter {

        Context context;
        int layoutResourceId;
        ArrayList<Comments> data = null;


        public CommentsAdapter(Context context, int layoutResourceId, ArrayList<Comments> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            final CommentHolder holder;
            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new CommentHolder(row);
                row.setTag(holder);
            } else {
                holder = (CommentHolder) row.getTag();
            }
            final Comments comment = data.get(position);
            if (!UtilityGeneral.isRegisteredUser(row.getContext())) {
                holder.btnLike.setEnabled(false);
                holder.btnDislike.setEnabled(false);
            } else {
                holder.btnLike.setEnabled(true);
                holder.btnDislike.setEnabled(true);
            }

            if (comment.getPhotoUrl() != null && !comment.getPhotoUrl().isEmpty()) {
                Glide.with(getContext())
                        .load(comment.getPhotoUrl())
                        .asBitmap()
                        .into(new BitmapImageViewTarget(holder.userImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                holder.userImage.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            } else {
                Glide.with(getContext())
                        .load(R.drawable.defaultavatar)
                        .asBitmap()
                        .into(new BitmapImageViewTarget(holder.userImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                holder.userImage.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            }

            holder.Name.setText(comment.getUserName());
            holder.Comment.setText(comment.getCommentText());

            if (comment.getUserLike() != null)
                holder.LikeNumber.setText(String.valueOf(comment.getUserLike().size()));
            else
                holder.LikeNumber.setText("0");
            if (comment.getUserDislike() != null)
                holder.DislikeNumber.setText(String.valueOf(comment.getUserDislike().size()));
            else
                holder.DislikeNumber.setText("0");
            holder.Date.setText(comment.getReadableTime());
            if (comment.getUserLike().containsKey(UtilityGeneral.loadUser(context).getObjectId()))
                //    holder.btnLike.setBackgroundResource(R.drawable.likegreen);
                holder.btnLike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.likegreen));
            else
                //   holder.btnLike.setBackgroundResource(R.drawable.likegray);
                holder.btnLike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.likegray));
            if (comment.getUserDislike().containsKey(UtilityGeneral.loadUser(context).getObjectId()))
                //   holder.btnDislike.setBackgroundResource(R.drawable.dislikered);
                holder.btnDislike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.dislikered));

            else
                holder.btnDislike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.dislikegray));
            // holder.btnDislike.setBackgroundResource(R.drawable.dislikegray);
            holder.btnLike.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UtilityFirebase.updateComment(comment, UtilityGeneral.loadUser(v.getContext()).getObjectId(), true);
                        }
                    });

            holder.btnDislike.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UtilityFirebase.updateComment(comment, UtilityGeneral.loadUser(v.getContext()).getObjectId(), false);
                        }
                    });

            return row;
        }
    }

    class CommentHolder {
        public TextView Name, Comment, Date, LikeNumber, DislikeNumber;
        public ImageButton btnLike, btnDislike;
        ImageView userImage;

        public CommentHolder(View rootView) {
            Name = (TextView) rootView.findViewById(R.id.txtName);
            Comment = (TextView) rootView.findViewById(R.id.txtComment);
            Date = (TextView) rootView.findViewById(R.id.txtTime);
            LikeNumber = (TextView) rootView.findViewById(R.id.txtLikeNumber);
            DislikeNumber = (TextView) rootView.findViewById(R.id.txtDisikeNumber);
            btnLike = (ImageButton) rootView.findViewById(R.id.btnLike);
            btnDislike = (ImageButton) rootView.findViewById(R.id.btnDislike);
            userImage = (ImageView) rootView.findViewById(R.id.user_avatar);

        }
    }