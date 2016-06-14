package com.app3roodk.UI.Shop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
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
import com.app3roodk.UtilityGeneral;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.List;

public class CreateShopFragment extends Fragment {

    static public LatLng latLngShop;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String mlogoId = null;
    Shop shop;
    List<Address> addresses;
    private TextView AddressFromMap;
    private EditText inputShopName, inputWorkingTime, inputAddressInfo, inputContacts;
    private TextInputLayout inputLayoutShopName, inputLayoutWorkingTime, inputLayoutAddressInfo, inputLayoutContacts;
    private Button createShop, btnChangeLocation;

    private ImageButton addShopLogo;
    private Switch haveAlogo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.creatshop_layout, container, false);
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
                Toast.makeText(getActivity(), "هيختار لوجو", Toast.LENGTH_SHORT).show();
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
                    final List<Address> tempAddresses = UtilityGeneral.getCurrentGovAndCity(getActivity(), latLngShop);
                    if (getActivity() == null)
                        return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                AddressFromMap.setText(tempAddresses.get(0).getAddressLine(3) + " - " + tempAddresses.get(0).getAddressLine(2) + " - " + tempAddresses.get(0).getAddressLine(1) + " - " + tempAddresses.get(0).getAddressLine(0));
                            } catch (Exception ex) {
                                Log.e("CreateShopFragment", ex.getMessage());
                            }
                        }
                    });
                    addresses = UtilityGeneral.getCurrentGovAndCityInEnglish(getActivity(), latLngShop);
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
                Uri u = data.getData();
                onSelectFromGalleryResult(data, u);
            } else if (requestCode == REQUEST_CAMERA) {
                Uri u = data.getData();
            }
        }
    }

    private void onSelectFromGalleryResult(Intent data, Uri uri) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};

        Cursor cursor = getActivity().getContentResolver().query(selectedImageUri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        setLogo(bm, uri);
    }


    private void setLogo(Bitmap thumbnail, Uri uri) {

        addShopLogo.setImageBitmap(thumbnail);
        addShopLogo.setAlpha(0.4f);
        new MyImgurUploadTask(uri, addShopLogo).execute();
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
            Toast.makeText(getActivity(), "يجب تحديد لوجو!", Toast.LENGTH_SHORT).show();

            return;
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
        shop.setCity(addresses.get(0).getAddressLine(2));
        shop.setGovernate(addresses.get(0).getAddressLine(3));
        shop.setLon(String.valueOf(latLngShop.longitude));
        shop.setLat(String.valueOf(latLngShop.latitude));
        shop.setCreatedAt(UtilityGeneral.getCurrentDate(new Date()));
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Shops").child(UtilityGeneral.loadUser(getActivity()).getObjectId());
        shop.setObjectId(myRef.push().getKey());
        myRef.child(shop.getObjectId()).setValue(shop, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError !=null) {
                    Toast.makeText(getActivity(), "حصل مشكله شوف النت ", Toast.LENGTH_SHORT).show();
                    Log.e("Frebaaase", databaseError.getMessage());
                }
                else
                {
                    Toast.makeText(getActivity(), "تم إضافه المحل، شكرا لك", Toast.LENGTH_SHORT).show();
                    UtilityGeneral.saveShop(getActivity(), shop);
                    getActivity().onBackPressed();
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

    private class MyImgurUploadTask extends ImgurUploadTask {
        String mImgurUrl = null;
        ImageButton imageButton;

        public MyImgurUploadTask(Uri imageUri, ImageButton addShopLogo) {
            super(imageUri, getActivity());
            this.imageButton = addShopLogo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String imageId) {
            super.onPostExecute(imageId);
            if (imageId != null) {
                mImgurUrl = "http://i.imgur.com/" + imageId + ".jpg";
                addShopLogo.setAlpha(1.0f);
                Toast.makeText(getActivity(), mImgurUrl + "", Toast.LENGTH_LONG).show();
                mlogoId = mImgurUrl;
            } else {
                mImgurUrl = null;
                Toast.makeText(getActivity(), "imgur_upload_error", Toast.LENGTH_LONG).show();
            }
        }
    }
}
