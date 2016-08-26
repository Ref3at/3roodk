package com.app3roodk.UI.Offer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.app3roodk.Constants;
import com.app3roodk.Imgur.ImgurUploadTask;
import com.app3roodk.R;
import com.app3roodk.Schema.Item;
import com.app3roodk.Schema.Offer;
import com.app3roodk.Schema.Shop;
import com.app3roodk.UI.Shop.ListShopsActivity;
import com.app3roodk.UtilityFirebase;
import com.app3roodk.UtilityGeneral;
import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.dd.morphingbutton.MorphingButton;
import com.dd.morphingbutton.impl.IndeterminateProgressButton;
import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddNewOfferFragment extends Fragment {


    Uploadclass imageForFacebookPost;
    String gifTag = null;
    String gifUrl = null;

    Switch fbSwitch;

    HashMap<Integer, UiItem> mapOfItems;

    List<String> framesId;

    Uri priceUri, lastFrameUri;

    Boolean initCode = true;
    int rc;
    boolean postGif = false;
    boolean postImage = false;
    Cloudinary cloudinary;
    TextView txt_InactiveShops;

    //********************
    ArrayList<Item> items_list = new ArrayList<>();
    ArrayList<String> lstShopsString;
    ArrayList<Shop> lstShops;
    Offer mOffer;
    String[] durtation_list, cat_list;
    LinearLayout itemsContainer;
    BetterSpinner duration_spinner, category_spinner, shops_spinnner;
    IndeterminateProgressButton btnMorph_publishOffer;
    IndeterminateProgressButton button;
    private String m_Text = "";
    private int mMorphCounter1 = 1;
    private EditText inputName, inputDesc;
    private TextInputLayout inputLayoutName, inputLayoutDesc;
    private Button publishOffer, previewOffer, addNewItem;
    private String mImgurUrl;
    private Uri mFileUri = null;

    /**
     * returns the bytesize of the give bitmap
     */
    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    static public String getCurrentDate(Date date) {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US).format(date);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity());

        Map config = new HashMap();
        config.put("cloud_name", "app3roodk");
        config.put("api_key", "936844595166333");
        config.put("api_secret", "vnIDUi3QVRk-a_wnJjFkGDslxvM");
        cloudinary = new Cloudinary(config);


        lastFrameUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.lastframe)
                + '/' + getResources().getResourceTypeName(R.drawable.lastframe) + '/' + getResources().getResourceEntryName(R.drawable.lastframe));


    }

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(getActivity(), "275139032860396");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mOffer = new Offer();

        View rootView = inflater.inflate(R.layout.fragment_add_new_offer, container, false);

        UtilityFirebase.updateUserNoOfAvailableOffers(getActivity(), UtilityGeneral.getCurrentYearAndWeek());

        txt_InactiveShops = (TextView) rootView.findViewById(R.id.txt_inactive_shops);


        final RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.radio_group);

        final RadioButton radioGif = (RadioButton) rootView.findViewById(R.id.radio_gif);

        radioGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postGif = true;
                postImage = false;
                imageForFacebookPost = null;
                for (UiItem item : mapOfItems.values()) {

                    for (Uploadclass uploadclass : item.imagesMap.values()) {


                        uploadclass.radioBox.setVisibility(View.INVISIBLE);
                        uploadclass.facebookImage.setVisibility(View.INVISIBLE);

                    }

                }
            }
        });

        RadioButton radioImage = (RadioButton) rootView.findViewById(R.id.radio_image);

        radioImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postImage = true;
                postGif = false;
                Toast.makeText(getActivity(), "من فضلك حدد صوره للبوست", Toast.LENGTH_LONG).show();


                boolean requstfocusForFirstItem = true;
                for (UiItem item : mapOfItems.values()) {

                    if (requstfocusForFirstItem) {
                        item.getRootV().requestFocus();
                        requstfocusForFirstItem = false;
                    }
                    for (Uploadclass uploadclass : item.imagesMap.values()) {
                        uploadclass.radioBox.setVisibility(View.VISIBLE);

                    }

                }


            }
        });

        fbSwitch = (Switch) rootView.findViewById(R.id.facebook_switch);

        fbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    radioGroup.setVisibility(View.VISIBLE);
                    radioGif.setChecked(true);
                    postGif = true;
                    // The toggle is enabled
                } else {
                    // The toggle is disabled
                    radioGroup.setVisibility(View.GONE);
                    postGif = false;
                    postImage = false;

                    for (UiItem item : mapOfItems.values()) {


                        for (Uploadclass uploadclass : item.imagesMap.values()) {
                            uploadclass.radioBox.setVisibility(View.INVISIBLE);
                            uploadclass.facebookImage.setVisibility(View.INVISIBLE);

                        }

                    }

                }
            }
        });


        framesId = new ArrayList<>();

        mapOfItems = new HashMap<>();

        gifTag = UUID.randomUUID().toString();


        addNewItem = (Button) rootView.findViewById(R.id.add_new_item);
        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inflateNewItem();
                if (itemsContainer.getChildCount() == 3) {
                    addNewItem.setVisibility(View.GONE);
                }
            }
        });

        itemsContainer = (LinearLayout) rootView.findViewById(R.id.items_container);

        inputLayoutName = (TextInputLayout) rootView.findViewById(R.id.input_layout_offername);
        inputLayoutDesc = (TextInputLayout) rootView.findViewById(R.id.input_layout_desc);

        inputName = (EditText) rootView.findViewById(R.id.input_offername);
        inputDesc = (EditText) rootView.findViewById(R.id.input_desc);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputDesc.addTextChangedListener(new MyTextWatcher(inputDesc));

        duration_spinner = (BetterSpinner) rootView.findViewById(R.id.duration_spinner);

        durtation_list = getResources().getStringArray(R.array.days_array);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, durtation_list);

        duration_spinner.setAdapter(adapter);

        category_spinner = (BetterSpinner) rootView.findViewById(R.id.cat_spinner);

        cat_list = getResources().getStringArray(R.array.cat_array);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, cat_list);

        category_spinner.setAdapter(adapter2);

        shops_spinnner = (BetterSpinner) rootView.findViewById(R.id.shop_spinner);
        lstShopsString = new ArrayList<>();
        lstShops = UtilityGeneral.loadShopsList(getActivity());
        for (Shop shop : lstShops) {

            if (!shop.isShopActive()) {
                lstShopsString.add(shop.getName() + " (غير مُفعل!)");
            } else {
                lstShopsString.add(shop.getName());

            }

        }
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, lstShopsString);
        shops_spinnner.setAdapter(adapter3);


        StringBuffer inactiveShopsMsg = new StringBuffer("لا تستطيع اضافه عروض لمحلات ( ");

        int x = 0;
        for (Shop shop : lstShops) {

            if (!shop.isShopActive()) {
                if (x != 0) {
                    inactiveShopsMsg.append(" ," + shop.getName());
                } else {
                    inactiveShopsMsg.append(shop.getName() + " ");
                }
                x++;
            }

        }

        inactiveShopsMsg.append(") ");

        inactiveShopsMsg.append("\n من فضلك أذهب للتفعيل");


        txt_InactiveShops.setText(inactiveShopsMsg);

        if (x > 0) {
            txt_InactiveShops.setVisibility(View.VISIBLE);
        }


        txt_InactiveShops.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ListShopsActivity.class));
            }
        });


        previewOffer = (Button) rootView.findViewById(R.id.btn_previewadd);
        previewOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "المفروض الاعلان يتعرض للمراجعه", Toast.LENGTH_SHORT).show();
            }
        });

        // publishOffer = (Button) rootView.findViewById(R.id.btn_publishadd);

        btnMorph_publishOffer = (IndeterminateProgressButton) rootView.findViewById(R.id.btn_publishadd);
        btnMorph_publishOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (UtilityGeneral.getNumberOfAvailableOffers(getActivity(), UtilityGeneral.getCurrentYearAndWeek()) <= 0) {
                    showMessage("عفواً لايمكنك عمل أكثر من " + String.valueOf(Constants.NUMBER_OF_OFFERS_PER_WEEK) + " عروض فى الإسبوع");
                    return;
                }else
                SubmitNewOffer();



            }
        });
        morphToSquare(btnMorph_publishOffer, 0);

        return rootView;
    }

    private void onMorphButton1Clicked(final IndeterminateProgressButton btnMorph) {
        if (mMorphCounter1 == 0) {
            mMorphCounter1++;
            morphToSquare(btnMorph, 500);
        } else if (mMorphCounter1 == 1) {
            mMorphCounter1 = 0;
            simulateProgress1(btnMorph);

        }
    }

    private void morphToSquare(final IndeterminateProgressButton btnMorph, int duration) {
        MorphingButton.Params square = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(10)
                .width((int) getResources().getDimension(R.dimen.width_200))
                .height((int) getResources().getDimension(R.dimen.height_56))
                .color(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                .colorPressed(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark))
                .text("ضيف الاعلان");
        btnMorph.morph(square);
    }

    private void morphToSuccess(final IndeterminateProgressButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(500)
                .cornerRadius((int) getResources().getDimension(R.dimen.height_56))
                .width((int) getResources().getDimension(R.dimen.width_120))
                .height((int) getResources().getDimension(R.dimen.height_56))
                .color(ContextCompat.getColor(getActivity(), R.color.primary))
                .colorPressed(ContextCompat.getColor(getActivity(), R.color.primary_dark))
                .text("تم إضافه العرض، شكراً لك");
        btnMorph.morph(circle);

    }

    private void morphToFailure(final IndeterminateProgressButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(500)
                .cornerRadius((int) getResources().getDimension(R.dimen.height_56))
                .width((int) getResources().getDimension(R.dimen.width_120))
                .height((int) getResources().getDimension(R.dimen.height_56))
                .color(ContextCompat.getColor(getActivity(), R.color.accent))
                .colorPressed(ContextCompat.getColor(getActivity(), R.color.accent))
                .text("حدث مشكله فى الاتصال!");
        btnMorph.morph(circle);
    }

    private void simulateProgress1(@NonNull final IndeterminateProgressButton button) {
        this.button = button;

        int progressColor1 = ContextCompat.getColor(getActivity(), R.color.holo_blue_bright);
        int progressColor2 = ContextCompat.getColor(getActivity(), R.color.holo_green_light);
        int progressColor3 = ContextCompat.getColor(getActivity(), R.color.holo_orange_light);
        int progressColor4 = ContextCompat.getColor(getActivity(), R.color.holo_red_light);
        int color = ContextCompat.getColor(getActivity(), R.color.mb_gray);
        int progressCornerRadius = (int) getResources().getDimension(R.dimen.mb_corner_radius_4);
        int width = (int) getResources().getDimension(R.dimen.width_200);
        int height = (int) getResources().getDimension(R.dimen.height_8);
        int duration = 500;

        button.blockTouch(); // prevent user from clicking while button is in progress

        try {
            publishtoServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }




        button.morphToProgress(color, progressCornerRadius, width, height, duration, progressColor1, progressColor2,
                progressColor3, progressColor4);


    }

    private void inflateNewItem() {
        final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootView = inflater.inflate(R.layout.add_offer_item, null);
        itemsContainer.addView(rootView);

        final LinearLayout theView = (LinearLayout) rootView.findViewById(R.id.the_view);

        final TextView txtAfter = (TextView) rootView.findViewById(R.id.txtb);
        final TextView txtbefore = (TextView) rootView.findViewById(R.id.txtA);
        final TextView txtprecent = (TextView) rootView.findViewById(R.id.txtpr);
        final View dash = (View) rootView.findViewById(R.id.dash);


        final EditText inputPriceBefore = (EditText) rootView.findViewById(R.id.input_pricebefore);
        final EditText inputPriceAfter = (EditText) rootView.findViewById(R.id.input_priceafter);

        inputPriceAfter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!editable.toString().isEmpty()) {
                    theView.setVisibility(View.VISIBLE);
                    txtAfter.setText(editable.toString());

                    if (!inputPriceBefore.getText().toString().isEmpty()) {
                        String A = inputPriceAfter.getText().toString();
                        String B = inputPriceBefore.getText().toString();


                        String x = String.format("%.0f", (1 - (Double.parseDouble(A)) / (Double.parseDouble(B))) * 100) + "%";
                        txtprecent.setText(x);
                    } else {
                        txtprecent.setText("");
                    }
                } else {
                    txtAfter.setText("");
                    if (txtAfter.getText().equals("") && txtbefore.getText().equals("")) {
                        theView.setVisibility(View.INVISIBLE);
                    }


                }


            }
        });


        inputPriceBefore.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    theView.setVisibility(View.VISIBLE);
                    txtbefore.setText(editable.toString());
                    dash.setVisibility(View.VISIBLE);

                    if (!inputPriceAfter.getText().toString().isEmpty()) {
                        //   cardHolder.discount.setText(String.format("%.0f", (1 - (Double.parseDouble(lstOffers.get(position).getItems().get(0).getPriceAfter()) / Double.parseDouble(lstOffers.get(position).getItems().get(0).getPriceBefore()))) * 100) + "%");

                        String A = inputPriceAfter.getText().toString();
                        String B = inputPriceBefore.getText().toString();


                        String x = String.format("%.0f", (1 - (Double.parseDouble(A)) / (Double.parseDouble(B))) * 100) + "%";
                        txtprecent.setText(x);
                    } else {
                        txtprecent.setText("");
                    }
                } else {
                    txtbefore.setText("");
                    dash.setVisibility(View.GONE);
                    if (txtAfter.getText().equals("") && txtbefore.getText().equals("")) {
                        theView.setVisibility(View.INVISIBLE);
                    }

                }
            }
        });


        final UiItem item = new UiItem();
        item.setUniqueNo(Integer.parseInt(getCurrentDate(new Date()).substring(12, 16)));
        item.setRootV(rootView);
        mapOfItems.put(item.getUniqueNo(), item);

        rootView.findViewById(R.id.delete_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (final Uploadclass uploadclass : mapOfItems.get(item.getUniqueNo()).imagesMap.values()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                cloudinary.uploader().destroy(uploadclass.getPublic_id(), ObjectUtils.emptyMap());
                                mapOfItems.get(item.getUniqueNo()).imagesMap.remove(uploadclass.getUniqueKey());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                itemsContainer.removeView(rootView);
                addNewItem.setVisibility(View.VISIBLE);
            }
        });

        rootView.findViewById(R.id.add_new_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(item.getUniqueNo());
            }
        });
    }


    private void SubmitNewOffer() {
        if (!validateOfferName()) {
            return;
        }

        if (!validateOfferDesc()) {
            return;
        }

        if (!validateImagesIDs()) {
            return;
        }
        if (!validateDuration()) {
            return;
        }
        if (!validateCategory()) {
            return;
        }
        if (!validateShop()) {
            return;
        }

        prepareItems();

    }

    private void prepareItems() {
        boolean success = true;
        items_list.clear();


        for (UiItem uiItem : mapOfItems.values()) {
            Item item = new Item();

            EditText inputPriceBefore = (EditText) uiItem.getRootV().findViewById(R.id.input_pricebefore);
            EditText inputPriceAfter = (EditText) uiItem.getRootV().findViewById(R.id.input_priceafter);
            item.setPriceBefore(inputPriceBefore.getText().toString());
            item.setPriceAfter(inputPriceAfter.getText().toString());

            if (item.getPriceAfter().isEmpty()) {
                ((TextInputLayout) uiItem.getRootV().findViewById(R.id.input_layout_priceafter)).setError(getString(R.string.err_msg_priceBefore));
                requestFocus(inputPriceAfter);
                success = false;
            } else {
                ((TextInputLayout) uiItem.getRootV().findViewById(R.id.input_layout_priceafter)).setErrorEnabled(false);
            }
            if (item.getPriceBefore().isEmpty()) {
                ((TextInputLayout) uiItem.getRootV().findViewById(R.id.input_layout_pricebefore)).setError(getString(R.string.err_msg_priceBefore));
                requestFocus(inputPriceBefore);
                success = false;
            } else {
                ((TextInputLayout) uiItem.getRootV().findViewById(R.id.input_layout_pricebefore)).setErrorEnabled(false);
            }

            try {
                if (Double.parseDouble(item.getPriceAfter()) >= Double.parseDouble(item.getPriceBefore())) {
                    ((TextInputLayout) uiItem.getRootV().findViewById(R.id.input_layout_priceafter)).setError("السعر بعد الخصم يجب ان يكون اقل من السعر الأصلي للسلعة");
                    requestFocus(inputPriceAfter);
                    success = false;
                    return;
                } else {
                    ((TextInputLayout) uiItem.getRootV().findViewById(R.id.input_layout_priceafter)).setErrorEnabled(false);
                }
                if (Double.parseDouble(item.getPriceAfter()) <= 0) {
                    ((TextInputLayout) uiItem.getRootV().findViewById(R.id.input_layout_priceafter)).setError("االسعر بعد الخصم يجب ان يكون اعلى من صفر");
                    requestFocus(inputPriceAfter);
                    success = false;
                } else {
                    ((TextInputLayout) uiItem.getRootV().findViewById(R.id.input_layout_priceafter)).setErrorEnabled(false);
                }
            } catch (Exception ex) {
            }

            ArrayList<String> imagesURLs = new ArrayList<>();
            for (final Uploadclass uploadclass : uiItem.imagesMap.values()) {
                imagesURLs.add(uploadclass.getUrl());
            }

            item.setImagePaths(imagesURLs);

            items_list.add(item);
        }

        if (!success) return;

        onMorphButton1Clicked(btnMorph_publishOffer);

    }

    private Boolean validateOfferName() {

        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private Boolean validateOfferDesc() {

        if (inputDesc.getText().toString().trim().isEmpty()) {
            inputLayoutDesc.setError(getString(R.string.err_msg_desc));
            requestFocus(inputDesc);
            return false;
        } else {
            inputLayoutDesc.setErrorEnabled(false);
        }

        return true;
    }

    private Boolean validateImagesIDs() {
        /*for (Map.Entry<String, MyImgurUploadTask> entry : mapImageUploading.entrySet()) {
            if (!entry.getValue().success) {
                showMessage("يجب الإنتظار حتى يتم رفع كل الصور");
                return false;
            }
        }*/
        if (itemsContainer.getChildCount() == 0) {
            showMessage("يجب إضافة سلعة واحده على الأقل");
            return false;
        }

        for (UiItem item : mapOfItems.values()) {

            if (item.imagesMap.size() == 0) {
                showMessage("يجب إضافة صورة على الأقل لكل عرض");
                return false;

            }
        }

        return true;

    }

    private Boolean validateDuration() {


        if (duration_spinner.getText().toString().isEmpty()) {

            Toast.makeText(getActivity(), "يجب تحديد مده للعرض", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private Boolean validateCategory() {


        if (category_spinner.getText().toString().isEmpty()) {

            Toast.makeText(getActivity(), "يجب اخيار قسم للعرض", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private Boolean validateShop() {


        if (shops_spinnner.getText().toString().isEmpty()) {

            Toast.makeText(getActivity(), "يجب اخيار المحل", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void selectImage(final int i) {

        final CharSequence[] items = {"إلتقط صوره!", "إختار صوره",
                "إلغاء"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ضيف صور العرض");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("إلتقط صوره!")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mFileUri);
                    startActivityForResult(intent, i);
                } else if (items[item].equals("إختار صوره")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "إختار صوره"),
                            i);
                } else if (items[item].equals("إلغاء")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (initCode) { // this boolean for passing the requestCode through cropActivity without change
            rc = requestCode;
            initCode = false;
        }
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode != CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("3roodk").setAutoZoomEnabled(true)
                        .setAspectRatio(540, 400).setFixAspectRatio(true).setOutputCompressQuality(50)
                        .start(getContext(), this);
                //.start(getActivity());
            } else {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                inflateNewImage(resultUri, rc);

            }

        }
    }


    private void inflateNewImage(final Uri uri, int requestCode) {
        initCode = true;
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootImage = inflater.inflate(R.layout.image_group, null);

        // final String key = UUID.randomUUID().toString();
        final UiItem item = mapOfItems.get(requestCode);

        LinearLayout lytImagesGroupContainer = null;
        ImageButton item_delete_btn = null;
        if (itemsContainer != null) {
            lytImagesGroupContainer = (LinearLayout) mapOfItems.get(requestCode).getRootV().findViewById(R.id.imgscontainer);
            lytImagesGroupContainer.addView(rootImage, lytImagesGroupContainer.getChildCount() - 1);
            item_delete_btn = (ImageButton) mapOfItems.get(requestCode).getRootV().findViewById(R.id.delete_item);
        }

        if (lytImagesGroupContainer.getChildCount() == 4) {
            item.getRootV().findViewById(R.id.add_new_image).setVisibility(View.GONE);
        }

        final ImageView imgOffer = (ImageView) rootImage.findViewById(R.id.theimage);
        Glide.with(this).load(uri).into(imgOffer);
        imgOffer.setAlpha(0.5f);
        final ImageView imgDone = (ImageView) rootImage.findViewById(R.id.img_done);
        final ImageButton del_image = (ImageButton) rootImage.findViewById(R.id.delete_img);
        final ImageButton img_refresh = (ImageButton) rootImage.findViewById(R.id.img_refresh);
        final SpinKitView progress = (SpinKitView) rootImage.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        final ImageView facebookImageView = (ImageView) rootImage.findViewById(R.id.facebook_image);
        final RadioButton radioBox = (RadioButton) rootImage.findViewById(R.id.checkbox_facebookimage);

        //showMessageToast("جاري رفع الصورة");


        final ImageButton finalItem_delete_btn = item_delete_btn;

        final Uploadclass[] uploadImage = {new Uploadclass(uri, imgOffer, imgDone, del_image, img_refresh, progress, item_delete_btn, radioBox, facebookImageView)};
        uploadImage[0].setUniqueKey(UUID.randomUUID().toString());
        uploadImage[0].setParentItem(item);
        item.imagesMap.put(uploadImage[0].getUniqueKey(), uploadImage[0]);
        item.imagesMap.get(uploadImage[0].getUniqueKey()).execute();
        if (item_delete_btn != null) {
            item_delete_btn.setVisibility(View.GONE);
        }

        final ImageButton finalItem_delete_btn1 = item_delete_btn;
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalItem_delete_btn != null) {
                    finalItem_delete_btn.setVisibility(View.GONE);
                }
                del_image.setVisibility(View.GONE);
                img_refresh.setVisibility(View.GONE);
                item.imagesMap.remove(uploadImage[0].getUniqueKey());
                uploadImage[0] = new Uploadclass(uri, imgOffer, imgDone, del_image, img_refresh, progress, finalItem_delete_btn1, radioBox, facebookImageView);
                uploadImage[0].setParentItem(item);
                item.imagesMap.put(uploadImage[0].getUniqueKey(), uploadImage[0]);
                item.imagesMap.get(uploadImage[0].getUniqueKey()).execute();
                progress.setVisibility(View.VISIBLE);
            }
        });
        final LinearLayout fLytImagesGroupContainer = lytImagesGroupContainer;
        del_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cloudinary.uploader().destroy(
                                    item.imagesMap.get(uploadImage[0].getUniqueKey()).getPublic_id(), ObjectUtils.emptyMap());
                            item.imagesMap.remove(uploadImage[0].getUniqueKey());
                        } catch (IOException e) {

                            e.printStackTrace();
                        }
                    }
                }).start();
                fLytImagesGroupContainer.findViewById(R.id.add_new_image).setVisibility(View.VISIBLE);
                fLytImagesGroupContainer.removeView(rootImage);
            }
        });

        radioBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {

                    showDialdForImagePost(facebookImageView, radioBox, uploadImage[0]);


                    // The toggle is enabled
                } else {
                    // The toggle is disabled

                }

            }
        });
    }

    private void showDialdForImagePost(final ImageView imageView, final RadioButton radioButton, final Uploadclass uploadclass) {

        new AlertDialog.Builder(getActivity())
                .setTitle("تحديد صوره للبوست")
                .setMessage("هل تريد اختيار هذه الصوره للنشر على الفيس بوك ؟")
                .setPositiveButton("موافق", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                        hideCheckBoxes(imageView);

                        imageForFacebookPost = uploadclass;
                        postImage = true;
                        postGif = false;

                    }
                })
                .setNegativeButton("إعاده إختيار", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        radioButton.setChecked(false);
                        imageForFacebookPost = null;
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void hideCheckBoxes(ImageView imageView) {
        imageView.setVisibility(View.VISIBLE);
        for (UiItem item : mapOfItems.values()) {

            for (Uploadclass uploadclass : item.imagesMap.values()) {

                uploadclass.radioBox.setVisibility(View.INVISIBLE);
                uploadclass.radioBox.setChecked(false);


            }


        }

    }



    private Bitmap ConvertToBitmap(LinearLayout theView) {
        theView.setDrawingCacheEnabled(true);
        theView.buildDrawingCache();
        Bitmap map = theView.getDrawingCache();

        return map;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Bitmap bitmap = Bitmap.createScaledBitmap(inImage, 151, 86, false);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    public void createGif() {

        new Thread(new Runnable() {
            @Override
            public void run() {


                // cloudinary.url().type("multi").imageTag("bank");


                try {

                    Transformation transformation = new Transformation();
                    transformation.delay(1600);
                    Map delay = new HashMap();
                    delay.put("transformation", transformation);
                    gifUrl = (String) cloudinary.uploader().multi(gifTag, delay).get("url");
                    if (gifUrl != null) {
                        deleteFrames();
                        String param = "link";
                        postFacebok(param, gifUrl);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();
        //       pub.setVisibility(View.VISIBLE);
    }

    void deleteFrames() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String public_id : framesId) {

                    try {
                        cloudinary.uploader().destroy(public_id, ObjectUtils.emptyMap());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }




    public void postFacebok(String param, String value) {


        String pass;
        if (param.equals("url"))
            pass = "photos";
        else
            pass = "feed";


        String token = "EAAD6PMxRRuwBABAPSQhUEGE5BnQ1E6pzJotCeU7Hpk7R5DTH0R7kEOHMC9oAZCqwwzmEZAOzqYFHMi0lhSiuEwv15SwmSkzvOrZBwuR1ygsYwkiadpa1VwlZAJaJMGZB00XBuTiV9Khcluzvg4haqZAnQwdxZA2fZCKFVWQqycH4eAZDZD";
        String applicationId = "275139032860396";
        String userId = "1579805665655253";
        Collection<String> permissions = null;
        Collection<String> declinedPermissions = null;
        AccessTokenSource accessTokenSource = null;
        Date expirationTime = null;
        Date lastRefreshTime = null;


        AccessToken accessToken = new AccessToken(token, applicationId, userId, permissions, declinedPermissions, accessTokenSource, expirationTime, lastRefreshTime);


        String pgeId = "1591626347747816";
        Bundle params = new Bundle();
        params.putString(param, value);
        params.putString("message", "poooooooost" + getCurrentDate(new Date()));
        //    params.putString("scheduled_publish_time","1476983634");



        /* make the API call */

        new GraphRequest(
                accessToken,
                "/" + pgeId + "/" + pass,
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                        String xx = response.toString();
//                            postID = (String) response.getJSONObject().get("id");
                        //   showMsg("Done");
                        showMessage(xx);


                    }
                }
        ).executeAsync();


    }

    private void showMessage(String msg) {
        try {
            Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).show();
        } catch (Exception ex) {
        }

    }

    private void showMessageToast(String msg) {
        try {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
        }

    }

    public void publishtoServer() throws InterruptedException {

        if (postGif) { // prepare for posting gif
            int i = 1;
            int itemNo = 0;
            int imageNo = 0;

            for (UiItem item : mapOfItems.values()) {

                LinearLayout priceView = (LinearLayout) item.getRootV().findViewById(R.id.the_view);
                Bitmap bitmap = ConvertToBitmap(priceView);
                final Uri priceUri = getImageUri(getActivity(), bitmap);

                for (final Uploadclass uploadclass : item.imagesMap.values()) {

                    final int finalItemNo = itemNo;
                    final int finalImageNo = imageNo;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Transformation transformation = null;
                            transformation = new Transformation().height(400).crop("scale")
                                    .gravity("south_east").underlay(uploadclass.getPublic_id()).chain()
                                    .gravity("north_west").height(80).overlay("3roodk_Logo_etnsc5").crop("scale");
                        /*
                                    (String) cloudinary.uploader().upload(
                                            getApplicationContext().getContentResolver().openInputStream(priceUri),
                                            ObjectUtils.emptyMap()).get("public_id")*/
                            try {
                                String frameId = (String) cloudinary.uploader().upload(
                                        getActivity().getApplicationContext().getContentResolver().openInputStream(priceUri),
                                        //               ObjectUtils.asMap("public_id", "frame"+getCurrentDate(new Date()).substring(12, 16),"transformation", transformation, "tags", gifTag)).get("public_id");
                                        ObjectUtils.asMap("public_id", "frame-" + String.valueOf(finalItemNo) + "-" + String.valueOf(finalImageNo), "transformation", transformation, "tags", gifTag)).get("public_id");
                                framesId.add(frameId);
                                getActivity().getContentResolver().delete(priceUri, null, null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                    imageNo++;
                }

                if (i++ == mapOfItems.size()) {


                    final Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Transformation transformation = new Transformation();
                                        transformation.width(540).height(400);
                                        String lastFrameId = (String) cloudinary.uploader().upload(
                                                getActivity().getApplicationContext().getContentResolver().openInputStream(lastFrameUri),
                                                ObjectUtils.asMap("transformation", transformation, "public_id", "zframe", "tags", gifTag)).get("public_id");
                                        framesId.add(lastFrameId);
                                        createGif();
                                        Publish();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).start();

                        }
                    }, 5000);


                }

                itemNo++;
            }

        } else if (postImage && imageForFacebookPost != null) {

            LinearLayout priceView = (LinearLayout) imageForFacebookPost.getParentItem().getRootV().findViewById(R.id.the_view);
            Bitmap bitmap = ConvertToBitmap(priceView);
            final Uri priceUri = getImageUri(getActivity(), bitmap);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    Transformation transformation = null;
                    transformation = new Transformation().height(400).width(540).crop("scale")
                            .gravity("south_east").underlay(imageForFacebookPost.getPublic_id()).chain()
                            .gravity("north_west").height(80).overlay("3roodk_Logo_etnsc5").crop("scale");
                        /*
                                    (String) cloudinary.uploader().upload(
                                            getApplicationContext().getContentResolver().openInputStream(priceUri),
                                            ObjectUtils.emptyMap()).get("public_id")*/
                    try {
                        String frameUrl = (String) cloudinary.uploader().upload(
                                getActivity().getContentResolver().openInputStream(priceUri),
                                //               ObjectUtils.asMap("public_id", "frame"+getCurrentDate(new Date()).substring(12, 16),"transformation", transformation, "tags", gifTag)).get("public_id");
                                //  ObjectUtils.asMap("public_id", "frame-" + String.valueOf(finalItemNo) + "-" + String.valueOf(finalImageNo), "transformation", transformation, "tags", gifTag)).get("public_id");
                                ObjectUtils.asMap("transformation", transformation)).get("url");
                        getActivity().getContentResolver().delete(priceUri, null, null);
                        String param = "url";
                        postFacebok(param, frameUrl);
                        Publish();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();


            //prepare for posting photo


        } else if (fbSwitch.isChecked()) {
            if (imageForFacebookPost == null)
                Toast.makeText(getActivity(), "من فضلك قم بتحديد صوره للبوست!", Toast.LENGTH_SHORT).show();
        }else
            Publish();


    }

    private void Publish() {
        try {

            mOffer.setCategoryName(UtilityGeneral.getCategoryEnglishName(category_spinner.getText().toString()));
            mOffer.setTitle(inputName.getText().toString());
            mOffer.setDesc(inputDesc.getText().toString());
            mOffer.setPeriod(duration_spinner.getText().toString());
            mOffer.setItems(items_list);
            Shop shop = null;
            for (Shop sh : lstShops)
                if (sh.getName().equals(shops_spinnner.getText().toString())) {
                    shop = sh;
                    break;
                }

            if (shop == null) {

                goToActivateShopDialog();
                morphToSquare(button, 500);
                button.unblockTouch();
                mMorphCounter1 = 1;

                return;
            }

            mOffer.setShopId(shop.getObjectId());
            mOffer.setShopName(shop.getName());
            mOffer.setLat(shop.getLat());
            mOffer.setLon(shop.getLon());
            mOffer.setCreatedAt(UtilityGeneral.getCurrentDate(new Date()));
            mOffer.setAverageRate("0");
            mOffer.setTotalRate("0");
            mOffer.setUserId(UtilityGeneral.loadUser(getActivity()).getObjectId());
            mOffer.setCity(shop.getCity());
            mOffer.setUserNotificationToken(UtilityGeneral.loadUser(getActivity()).getNotificationToken());
            Calendar cal = Calendar.getInstance();
            for (int i = 0; i < durtation_list.length; i++) {
                if (mOffer.getPeriod().equals(durtation_list[i])) {
                    if (i == 12)
                        cal.add(Calendar.DAY_OF_MONTH, 14);
                    else
                        cal.add(Calendar.DAY_OF_MONTH, i + 1);
                }
            }
            mOffer.setEndTime(UtilityGeneral.getCurrentDate(cal.getTime()));

            if (UtilityGeneral.isOnline(getActivity())) {

                UtilityFirebase.createNewOffer(mOffer, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            //  showMessage("حصل مشكله شوف النت ");

                            morphToFailure(button);
                            button.unblockTouch();

//                        Log.e("NewOfferFailed", databaseError.getMessage());
                        } else {
                            UtilityFirebase.createNewOfferExists(mOffer, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    UtilityFirebase.decreaseNumberOfOffersAvailable(getActivity(), new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            // showMessage("تم إضافه العرض، شكرا لك");

                                            morphToSuccess(button);
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    getActivity().onBackPressed();

                                                }
                                            }, 5000);

                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            } else {
                morphToFailure(button);
                button.unblockTouch();
            }
        } catch (Exception ex) {
//            Log.e("AddNewOfferFrag", ex.getMessage());
        }
    }

    private void goToActivateShopDialog() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        startActivity(new Intent(getActivity(), ListShopsActivity.class));


                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        String shopNameFromSpinner = shops_spinnner.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("لا يمكن إضافه عرض لمحل غير مفعل، إذهب لتفعيل محل " + shopNameFromSpinner.substring(0, shopNameFromSpinner.indexOf("(")) + "!").setPositiveButton("تفعيل", dialogClickListener)
                .setNegativeButton("رجوع", dialogClickListener).show();
    }

    class UiItem {
        HashMap<String, Uploadclass> imagesMap = new HashMap<>();
        private int uniqueNo;
        private View rootV;

        public int getUniqueNo() {
            return uniqueNo;
        }

        public void setUniqueNo(int uniqueNo) {
            this.uniqueNo = uniqueNo;
        }

        public View getRootV() {
            return rootV;
        }

        public void setRootV(View rootV) {
            this.rootV = rootV;
        }
    }

    class Uploadclass extends AsyncTask<Void, Void, HashMap> {
        ImageView facebookImage;
        RadioButton radioBox;
        boolean done, success;
        ImageView imageView, imageViewdone;
        Uri uri;
        SpinKitView progress;
        ImageButton delete_item;
        ImageButton imgdel, imgRefresh;
        private String public_id;
        private String Url;
        private String uniqueKey;
        private UiItem parentItem;

        public Uploadclass(Uri imageUri, ImageView imageView, ImageView imgdn, ImageButton del, ImageButton ref, SpinKitView progress, ImageButton deleteItem, RadioButton rb, ImageView fbImage) {
            this.imageView = imageView;
            this.imageViewdone = imgdn;
            this.uri = imageUri;
            this.imgdel = del;
            this.imgRefresh = ref;
            this.progress = progress;
            this.delete_item = deleteItem;
            this.radioBox = rb;
            this.facebookImage = fbImage;
        }

        public UiItem getParentItem() {
            return parentItem;
        }

        public void setParentItem(UiItem parentItem) {
            this.parentItem = parentItem;
        }

        public boolean isDone() {
            return done;
        }

        public String getUniqueKey() {
            return uniqueKey;
        }

        public void setUniqueKey(String uniqueKey) {
            this.uniqueKey = uniqueKey;
        }

        public String getUrl() {
            return Url;
        }

        public void setUrl(String url) {
            Url = url;
        }

        public String getPublic_id() {
            return public_id;
        }

        public void setPublic_id(String public_id) {
            this.public_id = public_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Url = null;
            success = false;
            done = false;
        }

        @Override
        protected HashMap doInBackground(Void... voids) {
            HashMap uploadResult = null;
            try {
                uploadResult = (HashMap) cloudinary.uploader().upload(getActivity().getContentResolver()
                        .openInputStream(uri), ObjectUtils.emptyMap());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return uploadResult;
        }

        @Override
        protected void onPostExecute(HashMap response) {
            super.onPostExecute(response);

            progress.setVisibility(View.GONE);
            imgdel.setVisibility(View.VISIBLE);
            delete_item.setVisibility(View.VISIBLE);

            if (response != null && response.get("url") != null) {
                Url = (String) response.get("url");
                public_id = (String) response.get("public_id");

                imageView.setAlpha(1.0f);
                imageViewdone.setVisibility(View.VISIBLE);
                imgdel.setVisibility(View.VISIBLE);
                showMessageToast("تم رفع الصورة");
                done = true;
                success = true;
            } else {
                Url = null;
                done = true;
                success = false;
                imgRefresh.setVisibility(View.VISIBLE);
                showMessageToast("حدث خطأ أثناء رفع الصورة");
            }
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_offername:
                    validateOfferName();
                    break;
                case R.id.input_desc:
                    validateOfferDesc();
                    break;
            }
        }
    }


}