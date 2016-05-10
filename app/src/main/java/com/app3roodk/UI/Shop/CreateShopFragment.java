package com.app3roodk.UI.Shop;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.app3roodk.Back4App.UtilityRestApi;
import com.app3roodk.Imgur.ImgurUploadTask;
import com.app3roodk.R;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Refaat on 5/6/2016.
 */
public class CreateShopFragment extends Fragment {

    int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    String mlogoId = null;


    private TextView AddressFromMap;
    private EditText inputShopName, inputWorkingTime, inputAddressInfo;
    private TextInputLayout inputLayoutShopName, inputLayoutWorkingTime, inputLayoutAddressInfo;
    private Button createShop;

    private ImageButton addShopLogo;
    private Switch haveAlogo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.creatshop_layout, container, false);


        inputLayoutShopName = (TextInputLayout) rootView.findViewById(R.id.input_layout_shopname);
        inputLayoutWorkingTime = (TextInputLayout) rootView.findViewById(R.id.input_layout_workingtime);
        inputLayoutAddressInfo = (TextInputLayout) rootView.findViewById(R.id.input_layout_addressinfo);

        inputShopName = (EditText) rootView.findViewById(R.id.input_shopname);
        inputWorkingTime = (EditText) rootView.findViewById(R.id.input_workingtime);
        inputAddressInfo = (EditText) rootView.findViewById(R.id.input_addressinfo);


        inputShopName.addTextChangedListener(new MyTextWatcher(inputShopName));
        inputWorkingTime.addTextChangedListener(new MyTextWatcher(inputWorkingTime));
        inputAddressInfo.addTextChangedListener(new MyTextWatcher(inputAddressInfo));

        addShopLogo = (ImageButton) rootView.findViewById(R.id.imgbtn_addlogo);
        addShopLogo.setAlpha(0.5f);

        addShopLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "هيختار لوجو", Toast.LENGTH_SHORT).show();

                selectLogo();
            }
        });
        addShopLogo.setClickable(false);


        haveAlogo = (Switch) rootView.findViewById(R.id.hava_alogo_switch);
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


        createShop = (Button) rootView.findViewById(R.id.btn_create_shop);

        createShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitAndCreate();
            }
        });


        return rootView;
    }

    private void selectLogo() {


/*        final CharSequence[] items = {
                "إلتقط صوره!",

                "إختار صوره",
                "إلغاء"}; */

        final CharSequence[] items = {"إختار لوجو", "إلغاء"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ضيف لوجو لمحلك");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
               /* if (items[item].equals("إلتقط صوره!")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else */
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
                //               onCaptureImageResult(data, u);
            }
        }
    }
/*
    private void onCaptureImageResult(Intent data, Uri uri) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setLogo(thumbnail, uri);
    }
*/

    private void onSelectFromGalleryResult(Intent data, Uri uri) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        // Cursor cursor = managedQuery(selectedImageUri, projection, null, null, null);

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
        if (mlogoId == null && haveAlogo.isChecked()) {
            Toast.makeText(getActivity(), "يجب تحديد لوجو!", Toast.LENGTH_SHORT).show();

            return;
        }


        Create();
        Toast.makeText(getActivity(), "تم إنشاء محلك بنجاح", Toast.LENGTH_SHORT).show();
    }

    private void Create() {


        HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", inputShopName.getText().toString());
        map.put("address", inputAddressInfo.getText().toString());
        map.put("workingTime", inputWorkingTime.getText().toString());

        map.put("logoId", mlogoId);


        map.put("userId", "j7gPGenT7n");

        //from Shared Pref
        // we have two lon and lat
        // for user as a user this we get it on positing activity or any where
        // for user as merchants (shop fixed location)  this we get from this activity just one
        map.put("governate", "ggggggggggggggg");
        map.put("city", "cccccccccccccccccccc");
        map.put("lon", "looooooon");
        map.put("lat", "laaaaaaaaaaat");

        UtilityRestApi.createShop(getActivity(), map, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getActivity(), "حصل مشكله ما", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Toast.makeText(getActivity(), "نجااااااااااااااااح", Toast.LENGTH_SHORT).show();

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
                mImgurUrl = "http://i.imgur.com/" + imageId;

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
