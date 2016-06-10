package com.app3roodk.UI.DetailActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app3roodk.Back4App.CallRestApi;
import com.app3roodk.Back4App.UtilityRestApi;
import com.app3roodk.ObjectConverter;
import com.app3roodk.R;
import com.app3roodk.Schema.Comments;
import com.app3roodk.Schema.Offer;
import com.app3roodk.Schema.Shop;
import com.app3roodk.UI.FullScreenImage.FullScreenImageSlider;
import com.app3roodk.UtilityGeneral;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class DetailFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    Button btnFavorite, btnShare, btnShopWay, btnComment;
    TextView txtViews, txtSale, txtPriceBefore, txtPriceAfter, txtDay, txtHour, txtMinute, txtDescription,
            txtShopName, txtWorkTime, txtAddress, txtMobile, txtRate;
    EditText edtxtComment;
    ListView lsvComments;
    RatingBar ratebar;
    private SliderLayout mDemoSlider;
    Calendar cal;
    final static long minutesInMilli = 1000 * 60;
    final static long hoursInMilli = minutesInMilli * 60;
    final static long daysInMilli = hoursInMilli * 24;

    private Offer offer;
    private Shop shop;
    long timeInMilliseconds;
    Thread timer;
    private FirebaseListAdapter<Comments> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        init(rootView);
        fillViews();
        initCallApi();
        return rootView;
    }

    private void fillViews() {
        try {
            txtDescription.setText(offer.getDesc());
            txtShopName.setText(offer.getShopName());
            txtPriceAfter.setText(offer.getPriceAfter());
            txtPriceBefore.setText(offer.getPriceBefore());
            txtViews.setText(String.valueOf(offer.getViewNum()));
            txtRate.setText(String.valueOf(String.valueOf(offer.getAverageRate())));
            initSlider(offer.getImagePaths());
            txtSale.setText(
                    String.format("%.0f", (1 - (Double.parseDouble(offer.getPriceAfter()) / Double.parseDouble(offer.getPriceBefore()))) * 100)+"%");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments").child(offer.getObjectId());

            mAdapter = new FirebaseListAdapter<Comments>(getActivity(), Comments.class, R.layout.comment_item, ref) {
                @Override
                protected void populateView(View view, Comments comment, int position) {
                    CommentHolder holder = new CommentHolder(view);
                    holder.Name.setText(comment.getUserName());
                    holder.Comment.setText(comment.getCommentText());
                    holder.DislikeNumber.setText(String.valueOf(comment.getDislike()));
                    holder.LikeNumber.setText(String.valueOf(comment.getLike()));
                    holder.Date.setText(comment.getTime());
                }
            };
            lsvComments.setAdapter(mAdapter);
            btnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(edtxtComment.getText().toString().isEmpty())
                    {
                        Toast.makeText(getActivity(),"اكتب التعليق من فضلك اولاً",Toast.LENGTH_LONG);
                        return;
                    }
                    Comments comment = new Comments();
                    comment.setTime(UtilityGeneral.getCurrentDate(new Date()));
                    comment.setUserName(UtilityGeneral.loadUser(getActivity()).getName());
                    comment.setUserId(UtilityGeneral.loadUser(getActivity()).getObjectId());
                    comment.setCommentText(edtxtComment.getText().toString());
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Comments").child(offer.getObjectId());
                    comment.setObjectId(myRef.push().getKey());
                    myRef.child(comment.getObjectId()).setValue(comment, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError !=null) {
                                Toast.makeText(getActivity(), "حصل مشكله شوف النت ", Toast.LENGTH_SHORT).show();
                                Log.e("Frebaaase", databaseError.getMessage());
                            }
                            else
                            {
                                Toast.makeText(getActivity(), "تم إضافه التعليق، شكرا لك", Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            }
                        }
                    });
                }
            });
        } catch (Exception ex) {
        }
    }

    private void initShopViews() {
        try {
            txtAddress.setText(shop.getAddress());
            txtWorkTime.setText(shop.getWorkingTime());
            txtMobile.setText(shop.getContacts().get("Mobile"));
        } catch (Exception ex) {
        }
        btnShopWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(UtilityGeneral.DrawPathToCertainShop(
                        getContext(), UtilityGeneral.getCurrentLonAndLat(getContext()),
                        new LatLng(Double.parseDouble(shop.getLat()), Double.parseDouble(shop.getLon()))));
            }
        });
    }

    private void initCallApi() {
        UtilityRestApi.raiseOfferViewsByOne(getContext(), offer.getObjectId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
//                    offer.setViewNum(Integer.parseInt(new JSONObject(responseString).getString("viewNum")));
//                    for (int i = 0; i < CardsFragment.lstOffers.size(); i++)
//                        if (CardsFragment.lstOffers.get(i).getObjectId().equals(offer.getObjectId())) {
//                            CardsFragment.lstOffers.get(i).setViewNum(offer.getViewNum());
//                            txtViews.setText(String.valueOf(offer.getViewNum()));
//                        }
                } catch (Exception ex) {
                }
            }
        });
        CallRestApi.GET(getContext(), "Shop", offer.getShopId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    if (responseString.equals("{}")) {
                        Toast.makeText(getContext(), "No valid shop for this offer", Toast.LENGTH_LONG).show();
                        return;
                    }
                    shop = ObjectConverter.convertJsonToShop(new JSONObject(responseString));
                    initShopViews();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init(View rootView) {
        try {
            mDemoSlider = (SliderLayout) rootView.findViewById(R.id.imgOffer);

            btnFavorite = (Button) rootView.findViewById(R.id.btnFavorite);
            btnShare = (Button) rootView.findViewById(R.id.btnShare);
            btnShopWay = (Button) rootView.findViewById(R.id.btnShopWay);
            btnComment =(Button) rootView.findViewById(R.id.btnComment);
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

            offer = new Gson().fromJson(getActivity().getIntent().getStringExtra("offer"),Offer.class);
            cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, Integer.parseInt(offer.getEndTime().substring(0, 4)));
            cal.set(Calendar.MONTH, Integer.parseInt(offer.getEndTime().substring(4, 6)) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(offer.getEndTime().substring(6, 8)));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }
        catch (Exception ex){try{getActivity().finish();}catch (Exception exx){}}
    }

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

    private void initSlider(ArrayList<String> lstImages) {

        for (String image : lstImages) {
            TextSliderView textSliderView = new TextSliderView(getActivity());
            textSliderView
                    .image(image)
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Stack);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
    }

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
        i.putStringArrayListExtra("IMAGES", offer.getImagePaths());
        startActivity(i);
    }
}
class CommentHolder {
    public TextView Name, Comment, Date,LikeNumber,DislikeNumber;
    public Button btnLike,btnDislike;
    public Comment comment;

    public CommentHolder(View rootView) {
        Name = (TextView) rootView.findViewById(R.id.txtName);
        Comment= (TextView) rootView.findViewById(R.id.txtComment);
        Date = (TextView) rootView.findViewById(R.id.txtTime);
        LikeNumber = (TextView) rootView.findViewById(R.id.txtLikeNumber);
        DislikeNumber= (TextView) rootView.findViewById(R.id.txtDisikeNumber);
        btnLike = (Button) rootView.findViewById(R.id.btnLike);
        btnDislike = (Button) rootView.findViewById(R.id.btnDislike);
    }
}
