package com.app3roodk.UI.DetailActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.app3roodk.R;
import com.app3roodk.Schema.Comments;
import com.app3roodk.Schema.Offer;
import com.app3roodk.Schema.Shop;
import com.app3roodk.UI.FullScreenImage.FullScreenImageSlider;
import com.app3roodk.UI.Shop.ViewShopActivity;
import com.app3roodk.UtilityGeneral;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class DetailFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

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
    private ValueEventListener postListener;
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
        clickConfig();

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
                    String.format("%.0f", (1 - (Double.parseDouble(offer.getPriceAfter()) / Double.parseDouble(offer.getPriceBefore()))) * 100) + "%");
            ShopViewsAndUpdateViewsNumber();
            FirebaseDatabase.getInstance().getReference("Comments").child(offer.getObjectId()).addValueEventListener(postListener);
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
                Comments comment = new Comments();
                comment.setTime(UtilityGeneral.getCurrentDate(new Date()));
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
                            edtxtComment.setText("");
                        }
                    }
                });
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
    }

    private void ShopViewsAndUpdateViewsNumber() {
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
                        txtMobile.setText("لا يوجد"); */
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        } catch (Exception ex) {
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
        } catch (Exception ex) {
            try {
                getActivity().finish();
            } catch (Exception exx) {
            }
        }
    }

    private void initSlider(ArrayList<String> lstImages) {

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
        i.putStringArrayListExtra("IMAGES", offer.getImagePaths());
        startActivity(i);
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
