package com.app3roodk.UI.FavoritesDetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.app3roodk.UtilityGeneral;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Refaat on 6/22/2016.
 */
public class FavoritesDetailFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    final static long minutesInMilli = 1000 * 60;
    final static long hoursInMilli = minutesInMilli * 60;
    final static long daysInMilli = hoursInMilli * 24;
    ImageButton btnFavorite, btnShare;
    Button btnShopWay, btnComment;
    TextView txtViews, txtSale, txtPriceBefore, txtPriceAfter, txtDay, txtHour, txtMinute, txtDescription,
            txtShopName, txtWorkTime, txtAddress, txtMobile, txtRate;
    EditText edtxtComment;
    ListView lsvComments;
    RatingBar ratebar;
    Calendar cal;
    long timeInMilliseconds;
    Thread timer;
    CommentsAdapter adapter;
    ArrayList<Comments> lstComments;
    private SliderLayout mDemoSlider;
    private Offer offer;
    private Shop shop;
    private ValueEventListener postListener, offerListener;
    private Runnable updateTimerThread = new Runnable() {
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

        ShopViewsAndUpdateViewsNumber();
        //FirebaseDatabase.getInstance().getReference("Comments").child(offer.getObjectId()).addValueEventListener(postListener);
        //FirebaseDatabase.getInstance().getReference("Offers").child(offer.getCity()).child(offer.getCategoryName()).child(offer.getObjectId()).addValueEventListener(offerListener);
        clickConfig();

        new FetchFromDB(btnFavorite).execute();

        return rootView;
    }


    private void fillViews() {
        try {
            txtDescription.setText(offer.getDesc());
            txtShopName.setText(offer.getShopName());
            //txtPriceAfter.setText(offer.getPriceAfter());
            //txtPriceBefore.setText(offer.getPriceBefore());
            //
            // txtPriceAfter.setText(offer.getItems().get(0).getPriceAfter());
            // txtPriceBefore.setText(offer.getItems().get(0).getPriceBefore());
            txtViews.setText(String.valueOf(offer.getViewNum()));
            txtRate.setText(String.valueOf(String.valueOf(offer.getAverageRate())));
            initSlider(offer.getItems());

            //initSlider(offer.getItems().get(0).getImagePaths());
            //  initSlider(offer.getImagePaths());
            //  txtSale.setText(
            //          String.format("%.0f", (1 - (Double.parseDouble(offer.getPriceAfter()) / Double.parseDouble(offer.getPriceBefore()))) * 100) + "%");
            if (offer.getUsersRates().containsKey(UtilityGeneral.loadUser(getActivity()).getObjectId())) {
                ratebar.setRating(Float.parseFloat(offer.getUsersRates().get(UtilityGeneral.loadUser(getActivity()).getObjectId())));
            }
            //  txtSale.setText(
            //        String.format("%.0f", (1 - (Double.parseDouble(offer.getItems().get(0).getPriceAfter()) / Double.parseDouble(offer.getItems().get(0).getPriceBefore()))) * 100) + "%");

        } catch (Exception ex) {
        }
    }

    private void clickConfig() {
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UtilityGeneral.isRegisteredUser(getActivity())) {
                    Toast.makeText(getActivity(), "سجل الاول علشان تعرف تعمل تعليق", Toast.LENGTH_LONG).show();
                    return;
                }
                if (edtxtComment.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "اكتب التعليق من فضلك اولاً", Toast.LENGTH_LONG).show();
                    return;
                }
                final Comments comment = new Comments();
                comment.setTime(UtilityGeneral.getCurrentDate(new Date()));
                if (offer.getUserId().equals(UtilityGeneral.loadUser(getActivity()).getObjectId()))
                    comment.setUserName(offer.getShopName());
                else
                    comment.setUserName(UtilityGeneral.loadUser(getActivity()).getName());
                comment.setUserId(UtilityGeneral.loadUser(getActivity()).getObjectId());
                comment.setOfferId(offer.getObjectId());
                comment.setCommentText(edtxtComment.getText().toString());
                // hide keyboard
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                // end hide keyboard
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Comments").child(offer.getObjectId());
                comment.setObjectId(myRef.push().getKey());
                myRef.child(comment.getObjectId()).setValue(comment, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Toast.makeText(getActivity(), "حصل مشكله شوف النت ", Toast.LENGTH_SHORT).show();
                            Log.e("Frebaaase", databaseError.getMessage());
                        } else {
                            Toast.makeText(getActivity(), "تم إضافه التعليق، شكرا لك", Toast.LENGTH_SHORT).show();
                            if (!(offer.getUserNotificationToken() == null || offer.getUserNotificationToken().isEmpty()
                                    || offer.getShopName() == comment.getUserName())) {
                                HashMap<String, String> mapOffer = new HashMap<>();
                                mapOffer.put("offer", new Gson().toJson(offer));
                                UtilityCloudMessaging.sendNotification(getActivity(), offer.getUserNotificationToken(),
                                        UtilityCloudMessaging.COMMENT_TITLE, comment.getUserName() + UtilityCloudMessaging.COMMENT_BODY,
                                        offer.getObjectId(), mapOffer,"OPEN_ACTIVITY_1" ,new TextHttpResponseHandler() {
                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                                Log.e("asdasd", responseString);
                                            }

                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                                Log.e("fgsgsdf", responseString);
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
                if (!UtilityGeneral.isRegisteredUser(getActivity())) {
                    Toast.makeText(getActivity(), "سجل الاول علشان تعرف تقييم العرض", Toast.LENGTH_LONG).show();
                    return;
                }
                String userId = UtilityGeneral.loadUser(getActivity()).getObjectId();
                if (offer.getUsersRates().containsKey(userId)) {
                    float diff = rating - Float.parseFloat(offer.getUsersRates().get(userId));
                    offer.getUsersRates().remove(userId);
                    offer.getUsersRates().put(userId, String.valueOf(rating));
                    offer.setTotalRate(String.valueOf(Float.parseFloat(offer.getTotalRate()) + diff));
                    offer.setAverageRate(String.valueOf(Float.parseFloat(offer.getTotalRate()) / offer.getUsersRates().size()));
                } else {
                    offer.getUsersRates().put(userId, String.valueOf(rating));
                    offer.setTotalRate(String.valueOf(Float.parseFloat(offer.getTotalRate()) + rating));
                    offer.setAverageRate(String.valueOf(Float.parseFloat(offer.getTotalRate()) / offer.getUsersRates().size()));
                }

                HashMap<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/usersRates", offer.getUsersRates());
                childUpdates.put("/totalRate", offer.getTotalRate());
                childUpdates.put("/averageRate", offer.getAverageRate());
                FirebaseDatabase.getInstance().getReference("Offers").child(offer.getCity()).child(offer.getCategoryName()).child(offer.getObjectId()).updateChildren(childUpdates);
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
                Intent i = new Intent(getActivity(), ViewShopActivity.class);
                if (shop != null) {
                    i.putExtra("shop", new Gson().toJson(shop));
                }
                startActivity(i);
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
                                        btnFavorite.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                                        // if (mToast != null) {
                                        //    mToast.cancel();
                                        // }
                                        Toast.makeText(getActivity(), "Removed_from_favorites", Toast.LENGTH_SHORT).show();

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
                                        Toast.makeText(getContext(), "added", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(getActivity(), "i will share", Toast.LENGTH_SHORT).show();

            }
        });
    }

   /* private void ShopViewsAndUpdateViewsNumber() {
        try {
            FirebaseDatabase.getInstance().getReference("Shops").child(offer.getUserId()).child(offer.getShopId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    shop = dataSnapshot.getValue(Shop.class);
                    txtAddress.setText(shop.getAddress());
                    txtWorkTime.setText(shop.getWorkingTime());
                    txtMobile.setText(shop.getContacts());

                   /* if (shop.getContacts().containsKey("Mobile"))
                        txtMobile.setText(shop.getContacts().get("Mobile"));
                    else
                        txtMobile.setText("لا يوجد");/
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        } catch (Exception ex) {
        }

    } */

    private void ShopViewsAndUpdateViewsNumber() {

        if (offer.getShopInfoForFavoeites() != null) {


            HashMap<String, String> shopInfoHashmap = new HashMap<String, String>();

            shopInfoHashmap = ObjectConverter.fromStringToHashmapUsersRates(offer.getShopInfoForFavoeites());


            txtAddress.setText(shopInfoHashmap.get("shopAddress"));
            txtWorkTime.setText(shopInfoHashmap.get("shopWorkingTime"));
            txtMobile.setText(shopInfoHashmap.get("shopContacts"));

        }


    }

    private void init(View rootView) {
        try {
            mDemoSlider = (SliderLayout) rootView.findViewById(R.id.imgOffer);
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
            lsvComments = (ListView) rootView.findViewById(R.id.lsvComments);
            lstComments = new ArrayList<>();
            adapter = new CommentsAdapter(getActivity(), R.layout.comment_item, lstComments);
            lsvComments.setAdapter(adapter);
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
                    UtilityGeneral.setListViewHeightBasedOnChildren(lsvComments);
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

    private void initSlider(List<Item> lstItems) {

        for (int i = 0; i < lstItems.size(); i++) {

            for (int j = 0; j < lstItems.get(i).getImagePaths().size(); j++) {
                String imagePath = lstItems.get(i).getImagePaths().get(j);

                CustomImagesSlider customImagesSlider = new CustomImagesSlider(getActivity());
                customImagesSlider.setOffer_price_before(offer.getItems().get(i).getPriceBefore());
                customImagesSlider.setOffer_price_after(offer.getItems().get(i).getPriceAfter());
                customImagesSlider.setOffer_sale_perc(String.format("%.0f", (1 - (Double.parseDouble(offer.getItems().get(i).getPriceAfter())
                        / Double.parseDouble(offer.getItems().get(i).getPriceBefore()))) * 100) + "%");


                customImagesSlider
                        .description("حصرى على عروضك")
                        .image(imagePath)
                        .setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
                        .setOnSliderClickListener(this);
                mDemoSlider.addSlider(customImagesSlider);
            }
        }


        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Stack);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
    }

    /* private void initSlider(ArrayList<String> lstImages) {

        for (String image : lstImages) {
            TextSliderView textSliderView = new TextSliderView(getActivity());
            textSliderView
                    .image(image)
                    .setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
                    .setOnSliderClickListener(this);
            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Stack);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
    }
*/
    @Override
    public void onStop() {
        mDemoSlider.stopAutoCycle();
        if (timer.isAlive())
            timer.interrupt();
        super.onStop();
    }

    @Override
    public void onResume() {
        mDemoSlider.startAutoCycle();
        timer = new Thread(updateTimerThread);
        timer.start();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            FirebaseDatabase.getInstance().getReference("Comments").child(offer.getObjectId()).removeEventListener(postListener);
            FirebaseDatabase.getInstance().getReference("Offers").child(offer.getCity()).child(offer.getCategoryName()).child(offer.getObjectId()).removeEventListener(offerListener);
        } catch (Exception ex) {
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Intent i = new Intent(getActivity(), FullScreenImageSlider.class);
        //  i.putStringArrayListExtra("IMAGES", offer.getImagePaths());
        i.putStringArrayListExtra("IMAGES", offer.getItems().get(0).getImagePaths());
        startActivity(i);
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
                    R.drawable.abc_btn_rating_star_off_mtrl_alpha);

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
        CommentHolder holder;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new CommentHolder(row);
            row.setTag(holder);
        } else {
            holder = (CommentHolder) row.getTag();
        }
        final Comments comment = data.get(position);
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

        holder.btnLike.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userId = UtilityGeneral.loadUser(v.getContext()).getObjectId();
                        if (comment.getUserLike() != null) {
                            if (comment.getUserLike().contains(userId))
                                comment.getUserLike().remove(userId);
                            else
                                comment.getUserLike().add(userId);
                        } else {
                            comment.setUserLike(new ArrayList<String>());
                            comment.getUserLike().add(userId);
                        }
                        if (comment.getUserDislike() != null) {
                            if (comment.getUserDislike().contains(userId))
                                comment.getUserDislike().remove(userId);
                        }
                        FirebaseDatabase.getInstance().getReference("Comments").child(comment.getOfferId()).child(comment.getObjectId()).setValue(comment);
                    }
                });

        holder.btnDislike.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userId = UtilityGeneral.loadUser(v.getContext()).getObjectId();
                        if (comment.getUserDislike() != null) {
                            if (comment.getUserDislike().contains(userId))
                                comment.getUserDislike().remove(userId);
                            else
                                comment.getUserDislike().add(userId);
                        } else {
                            comment.setUserDislike(new ArrayList<String>());
                            comment.getUserDislike().add(userId);
                        }
                        if (comment.getUserLike() != null) {
                            if (comment.getUserLike().contains(userId))
                                comment.getUserLike().remove(userId);
                        }
                        FirebaseDatabase.getInstance().getReference("Comments").child(comment.getOfferId()).child(comment.getObjectId()).setValue(comment);
                    }
                });

        return row;
    }
}

class CommentHolder {
    public TextView Name, Comment, Date, LikeNumber, DislikeNumber;
    public Button btnLike, btnDislike;

    public CommentHolder(View rootView) {
        Name = (TextView) rootView.findViewById(R.id.txtName);
        Comment = (TextView) rootView.findViewById(R.id.txtComment);
        Date = (TextView) rootView.findViewById(R.id.txtTime);
        LikeNumber = (TextView) rootView.findViewById(R.id.txtLikeNumber);
        DislikeNumber = (TextView) rootView.findViewById(R.id.txtDisikeNumber);
        btnLike = (Button) rootView.findViewById(R.id.btnLike);
        btnDislike = (Button) rootView.findViewById(R.id.btnDislike);
    }
}
