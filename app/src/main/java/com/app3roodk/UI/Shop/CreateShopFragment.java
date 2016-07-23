package com.app3roodk.UI.Shop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.app3roodk.Imgur.ImgurUploadTask;
import com.app3roodk.MapsShopLocationActivity;
import com.app3roodk.R;
import com.app3roodk.Schema.Shop;
import com.app3roodk.UtilityFirebase;
import com.app3roodk.UtilityGeneral;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.maps.model.GeocodingResult;

import java.util.Date;
import java.util.List;

public class CreateShopFragment extends Fragment {

    static public LatLng latLngShop;
    int SELECT_FILE = 1;
    String mlogoId = null;
    Shop shop;
    GeocodingResult[] addresses;
    private TextView AddressFromMap;
    private EditText inputShopName, inputWorkingTime, inputAddressInfo, inputContacts;
    private TextInputLayout inputLayoutShopName, inputLayoutWorkingTime, inputLayoutAddressInfo, inputLayoutContacts;
    private Button createShop, btnChangeLocation;

    private ImageButton addShopLogo;
    private Switch haveAlogo;

    private MyImgurUploadTask myImgurUploadTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_shop, container, false);
        initViews(rootView);
        clickConfig();
        latLngShop = UtilityGeneral.getCurrentLonAndLat(getActivity());
        if (latLngShop == null) latLngShop = UtilityGeneral.loadLatLong(getActivity());
        return rootView;
    }

    private void initViews(View rootView) {
        AddressFromMap = (TextView) rootView.findViewById(R.id.txtShopAddressFromMap);

        inputLayoutShopName = (TextInputLayout) rootView.findViewById(R.id.input_layout_shopname);
        inputLayoutWorkingTime = (TextInputLayout) rootView.findViewById(R.id.input_layout_workingtime);
        inputLayoutAddressInfo = (TextInputLayout) rootView.findViewById(R.id.input_layout_addressinfo);
        inputLayoutContacts = (TextInputLayout) rootView.findViewById(R.id.input_layout_contacts);

        inputShopName = (EditText) rootView.findViewById(R.id.input_shopname);
        inputWorkingTime = (EditText) rootView.findViewById(R.id.input_workingtime);
        inputAddressInfo = (EditText) rootView.findViewById(R.id.input_addressinfo);
        inputContacts = (EditText) rootView.findViewById(R.id.input_contacts);

        inputShopName.addTextChangedListener(new MyTextWatcher(inputShopName));
        inputWorkingTime.addTextChangedListener(new MyTextWatcher(inputWorkingTime));
        inputAddressInfo.addTextChangedListener(new MyTextWatcher(inputAddressInfo));
        inputContacts.addTextChangedListener(new MyTextWatcher(inputContacts));

        addShopLogo = (ImageButton) rootView.findViewById(R.id.imgbtn_addlogo);
        addShopLogo.setAlpha(0.5f);
        addShopLogo.setClickable(false);

        createShop = (Button) rootView.findViewById(R.id.btn_create_shop);

        btnChangeLocation = (Button) rootView.findViewById(R.id.btnChangeAddress);

        haveAlogo = (Switch) rootView.findViewById(R.id.hava_alogo_switch);
    }

    private void clickConfig() {
        addShopLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveAlogo.isChecked())
                    selectLogo();
            }
        });

        haveAlogo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // perform logic
                    addShopLogo.setClickable(true);
                    addShopLogo.setAlpha(1.0f);
                } else {
                    addShopLogo.setClickable(false);
                    addShopLogo.setAlpha(0.4f);
                    if (myImgurUploadTask != null && !myImgurUploadTask.isDone()) {
                        myImgurUploadTask.cancel(true);
                    }
                    mlogoId = null;
                }
            }
        });

        createShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitAndCreate();
            }
        });

        btnChangeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MapsShopLocationActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        writeAddress();
    }

    private void writeAddress() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final GeocodingResult[] results = UtilityGeneral.getCurrentGovAndCityArabic(getActivity(), latLngShop);
                    if (getActivity() == null)
                        return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                AddressFromMap.setText(results[0].formattedAddress);
                            } catch (Exception ex) {
                                Log.e("CreateShopFragment", ex.getMessage());
                            }
                        }
                    });
                    addresses = UtilityGeneral.getCurrentGovAndCityArabic(getActivity(), latLngShop);
                }
            }).start();
        } catch (Exception ex) {
        }
    }

    private void selectLogo() {

        final CharSequence[] items = {"إختار لوجو", "إلغاء"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ضيف لوجو لمحلك");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("إختار لوجو")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "إختار لوجو"),
                            SELECT_FILE);
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

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                setLogo(data.getData());
            }
        }
    }

    private void setLogo(Uri uri) {

        Glide.with(getActivity()).load(uri).asBitmap().into(new BitmapImageViewTarget(addShopLogo) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                addShopLogo.setImageDrawable(circularBitmapDrawable);
            }
        });
        addShopLogo.setAlpha(0.5f);
        if (myImgurUploadTask != null && !myImgurUploadTask.isDone())
            myImgurUploadTask.cancel(true);
        if (myImgurUploadTask != null) mlogoId = null;
        myImgurUploadTask = new MyImgurUploadTask(uri, addShopLogo);
        myImgurUploadTask.execute();
    }

    private void SubmitAndCreate() {
        if (!validateShopName()) {
            return;
        }

        if (!validateWorkingTime()) {
            return;
        }

        if (!validateAddressInfo()) {
            return;
        }

        if (!validateContcats()) {
            return;
        }
        if (mlogoId == null && haveAlogo.isChecked()) {
            showMessage("يجب تحديد لوجو!");
            return;
        }
        if (haveAlogo.isChecked()) {
            if (!myImgurUploadTask.isDone()) {
                showMessage("يجب الإنتظار حتي يتم رفع الصورة!");
                return;
            }
            if (!myImgurUploadTask.isSuccess()) {
                showMessage("حدث خطأ أثناء رفع الصورة");
                return;
            }
        }
        Create();
    }

    private void Create() {
        shop = new Shop();
        shop.setContacts(inputContacts.getText().toString());
        shop.setName(inputShopName.getText().toString());
        shop.setAddress(inputAddressInfo.getText().toString());
        shop.setWorkingTime(inputWorkingTime.getText().toString());
        shop.setLogoId(mlogoId);
        shop.setCity(UtilityGeneral.getCity(addresses));
        if (shop.getCity() == null || shop.getCity().equals("null"))
            shop.setCity(UtilityGeneral.getGovernate(addresses));
        shop.setGovernate(UtilityGeneral.getGovernate(addresses));
        shop.setLon(String.valueOf(latLngShop.longitude));
        shop.setLat(String.valueOf(latLngShop.latitude));
        shop.setCreatedAt(UtilityGeneral.getCurrentDate(new Date()));
        UtilityFirebase.createNewShop(shop, UtilityGeneral.loadUser(getActivity()).getObjectId(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    showMessageToast("حصل مشكله شوف النت ");
                    Log.e("CreateNewShop", databaseError.getMessage());
                } else {
                    showMessageToast("تم إضافه المحل، شكرا لك");
                    UtilityGeneral.saveShop(getActivity(), shop);

                    getActivity().onBackPressed();
                    /*Attempt to invoke virtual method 'void android.support.v4.app.FragmentActivity.onBackPressed()' on a null object reference
                                                                   at com.app3roodk.UI.Shop.CreateShopFragment$8.onComplete(CreateShopFragment.java:288)*/
                }
            }
        });
    }

    private Boolean validateShopName() {

        if (inputShopName.getText().toString().trim().isEmpty()) {
            inputLayoutShopName.setError(getString(R.string.shop_err_msg_name));
            requestFocus(inputShopName);
            return false;
        } else {
            inputLayoutShopName.setErrorEnabled(false);
        }
        return true;
    }

    private Boolean validateWorkingTime() {

        if (inputWorkingTime.getText().toString().trim().isEmpty()) {
            inputLayoutWorkingTime.setError(getString(R.string.shop_err_msg_working));
            requestFocus(inputWorkingTime);
            return false;
        } else {
            inputLayoutWorkingTime.setErrorEnabled(false);
        }

        return true;
    }

    private Boolean validateAddressInfo() {

        if (inputAddressInfo.getText().toString().trim().isEmpty()) {
            inputLayoutAddressInfo.setError(getString(R.string.shop_err_msg_addressinfo));
            requestFocus(inputAddressInfo);
            return false;
        } else {
            inputLayoutAddressInfo.setErrorEnabled(false);
        }
        return true;
    }

    private Boolean validateContcats() {

        if (inputContacts.getText().toString().trim().isEmpty()) {
            inputLayoutContacts.setError(getString(R.string.shop_err_msg_contacts));
            requestFocus(inputContacts);
            return false;
        } else {
            inputLayoutContacts.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
                case R.id.input_shopname:
                    validateShopName();
                    break;
                case R.id.input_workingtime:
                    validateWorkingTime();
                    break;
                case R.id.input_addressinfo:
                    validateAddressInfo();
                    break;

                case R.id.input_contacts:
                    validateContcats();

                    break;
            }
        }
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

    private class MyImgurUploadTask extends ImgurUploadTask {
        boolean done, success;
        String mImgurUrl = null;
        ImageButton imageButton;

        public boolean isDone() {
            return done;
        }

        public boolean isSuccess() {
            return success;
        }

        public MyImgurUploadTask(Uri imageUri, ImageButton addShopLogo) {
            super(imageUri, getActivity());
            this.imageButton = addShopLogo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            done = false;
            success = false;
        }

        @Override
        protected void onPostExecute(String imageId) {
            super.onPostExecute(imageId);
            if (imageId != null) {
                mImgurUrl = "http://i.imgur.com/" + imageId + ".jpg";
                addShopLogo.setAlpha(1.0f);
                showMessage("تم رفع الصورة بنجاح");
                mlogoId = mImgurUrl;
                done = true;
                success = true;
            } else {
                mImgurUrl = null;
                done = true;
                success = false;
                showMessageToast("حصل مشكلة فى رفع الصورة");
            }
        }
    }
}
