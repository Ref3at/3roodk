package com.app3roodk.UI.Offer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app3roodk.Back4App.UtilityRestApi;
import com.app3roodk.Constants;
import com.app3roodk.Imgur.ImgurUploadTask;
import com.app3roodk.ObjectConverter;
import com.app3roodk.R;
import com.app3roodk.Schema.Offer;
import com.app3roodk.UtilityGeneral;
import com.loopj.android.http.TextHttpResponseHandler;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Refaat on 5/6/2016.
 */
public class AddNewOfferFragment extends Fragment {


    ArrayList<String> images_id = new ArrayList<>();


    Offer mOffer;

    String[] durtation_list, cat_list;

    int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    LinearLayout imgsContainer;
    BetterSpinner duration_spinner;
    BetterSpinner category_spinner;
    private EditText inputName, inputDesc, inputPriceBefore, inputPriceAfter;
    private TextInputLayout inputLayoutName, inputLayoutDesc, inputLayoutPriceBefore, inputLayoutPriceAfter;
    private Button publishOffer, previewOffer;
    private String mImgurUrl;
    private MyImgurUploadTask mImgurUploadTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mOffer = new Offer();


        View rootView = inflater.inflate(R.layout.add_offer_layout, container, false);

        ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.add_new_image);

        imgsContainer = (LinearLayout) rootView.findViewById(R.id.imgscontainer);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgsContainer.getChildCount() > 3) {
                    Toast.makeText(getActivity(), "you need to delete another image", Toast.LENGTH_SHORT).show();
                } else {
                    selectImage();
                }
            }
        });


        inputLayoutName = (TextInputLayout) rootView.findViewById(R.id.input_layout_offername);
        inputLayoutDesc = (TextInputLayout) rootView.findViewById(R.id.input_layout_desc);
        inputLayoutPriceBefore = (TextInputLayout) rootView.findViewById(R.id.input_layout_pricebefore);
        inputLayoutPriceAfter = (TextInputLayout) rootView.findViewById(R.id.input_layout_priceafter);

        inputName = (EditText) rootView.findViewById(R.id.input_offername);
        inputDesc = (EditText) rootView.findViewById(R.id.input_desc);
        inputPriceBefore = (EditText) rootView.findViewById(R.id.input_pricebefore);
        inputPriceAfter = (EditText) rootView.findViewById(R.id.input_priceafter);


        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputDesc.addTextChangedListener(new MyTextWatcher(inputDesc));
        inputPriceBefore.addTextChangedListener(new MyTextWatcher(inputPriceBefore));
        inputPriceAfter.addTextChangedListener(new MyTextWatcher(inputPriceAfter));

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


        previewOffer = (Button) rootView.findViewById(R.id.btn_previewadd);
        previewOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "المفروض الاعلان يتعرض للمراجعه", Toast.LENGTH_SHORT).show();
            }
        });

        publishOffer = (Button) rootView.findViewById(R.id.btn_publishadd);
        publishOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitNewOffer();
            }
        });


        return rootView;
    }

    private void SubmitNewOffer() {
        if (!validateOfferName()) {
            return;
        }

        if (!validateOfferDesc()) {
            return;
        }

        if (!validatePriceBefore()) {
            return;
        }

        if (!validatePriceAfter()) {
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

        Publish();
        Toast.makeText(getActivity(), "تم إضافه العرض، شكرا لك", Toast.LENGTH_SHORT).show();
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

    private Boolean validatePriceBefore() {

        if (inputPriceBefore.getText().toString().trim().isEmpty()) {
            inputLayoutPriceBefore.setError(getString(R.string.err_msg_priceBefore));
            requestFocus(inputPriceBefore);
            return false;
        } else {
            inputLayoutPriceBefore.setErrorEnabled(false);
        }

        return true;
    }

    private Boolean validatePriceAfter() {

        if (inputPriceAfter.getText().toString().trim().isEmpty()) {
            inputLayoutPriceAfter.setError(getString(R.string.err_msg_priceAfter));
            requestFocus(inputPriceAfter);
            return false;
        } else {
            inputLayoutPriceAfter.setErrorEnabled(false);
        }

        return true;
    }

    private Boolean validateImagesIDs() {

        if (images_id.size() < 1) {

            Toast.makeText(getActivity(), "يجب اضافه صور للعرض", Toast.LENGTH_SHORT).show();
            return false;
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

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void selectImage() {

        final CharSequence[] items = {"إلتقط صوره!", "إختار صوره",
                "إلغاء"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ضيف صور العرض");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("إلتقط صوره!")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("إختار صوره")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "إختار صوره"),
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
                onCaptureImageResult(data, u);
            }
        }
    }

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

        inflateNewImage(thumbnail, uri);
    }

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

        inflateNewImage(bm, uri);
    }

    private void inflateNewImage(Bitmap thumbnail, Uri uri) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootview = inflater.inflate(R.layout.image_group, null);
        imgsContainer.addView(rootview, 0);

        final ImageView imageView = (ImageView) rootview.findViewById(R.id.theimage);
        imageView.setImageBitmap(thumbnail);
        imageView.setAlpha(0.5f);

        ImageView imgdone = (ImageView) rootview.findViewById(R.id.img_done);

        Toast.makeText(getActivity(), "uploading...", Toast.LENGTH_SHORT).show();


        new MyImgurUploadTask(uri, imageView, imgdone, imgsContainer.getChildCount()).execute();


        ImageButton del_image = (ImageButton) rootview.findViewById(R.id.delete_img);
        del_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //**************************************************************************
                /// error here   the id come from asyns after we delete the image

                if (images_id.size() > 0 && !images_id.get(imgsContainer.indexOfChild(rootview)).isEmpty()) {
                    images_id.remove(imgsContainer.indexOfChild(rootview));
                }
                imgsContainer.removeView(rootview);

            }
        });


    }

    private void Publish() {

        mOffer.setCategoryId(getCategoryId(category_spinner));

        mOffer.setTitle(inputName.getText().toString());

        mOffer.setDesc(inputDesc.getText().toString());

        mOffer.setPriceBefore(inputPriceBefore.getText().toString());

        mOffer.setPriceAfter(inputPriceAfter.getText().toString());

        mOffer.setPeriod(duration_spinner.getText().toString());

        mOffer.setImagePaths(images_id);

        mOffer.setShopId("qEmoVmW9Ep");

        mOffer.setShopName("ShopHazem");

        Calendar cal= Calendar.getInstance();

        for (int i =0 ;i<durtation_list.length;i++)
        {
            if (mOffer.getPeriod().equals(durtation_list[i]))
            {
                if(i == 12)
                    cal.add(Calendar.DAY_OF_MONTH,14);
                else
                    cal.add(Calendar.DAY_OF_MONTH,i+1);
            }
        }
        mOffer.setEndTime(UtilityGeneral.getCurrentDate(cal.getTime()));

//        HashMap<String, String> map = new HashMap<String, String>();
//        map.put("title", inputName.getText().toString());
//        map.put("CategoryId", getCategoryId(category_spinner));
//        map.put("Desc", inputDesc.getText().toString());
//        map.put("PriceBefore", inputPriceBefore.getText().toString());
//        map.put("PriceAfter", inputPriceAfter.getText().toString());
//        map.put("Period", duration_spinner.getText().toString());
//        map.put("ImagePaths", images_id.toString());
//        map.put("ShopId", "qEmoVmW9Ep");
//        map.put("ShopName", "ShopHazem");

//        UtilityRestApi.addNewOffer(getActivity(), map, new TextHttpResponseHandler() {
        HashMap map = ObjectConverter.convertOfferToHashMap(mOffer);
        Log.v("11111111111",map.toString());
        UtilityRestApi.addNewOffer(getActivity(), ObjectConverter.convertOfferToHashMap(mOffer), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.v("11111111111", responseString);
                Toast.makeText(getActivity(), "حصل مشكله شوف النت ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Toast.makeText(getActivity(), "نجاااااااح", Toast.LENGTH_SHORT).show();


            }
        });


    }

    private String getCategoryId(BetterSpinner category_spinner) {
        String categoryName = category_spinner.getText().toString();
        String cat_id = null;

        switch (categoryName) {
            case "مطاعم":
                cat_id = Constants.CATEGORY_RESTAURANTS;
                break;
            case "سوبر ماركت":
                cat_id = Constants.CATEGORY_SUPER_MARKET;

                break;
            case "كافيه":
                cat_id = Constants.CATEGORY_CAFES;

                break;
            case "موبايل":
                cat_id = Constants.CATEGORY_MOBILES;

                break;
            case "كمبيوتر":
                cat_id = Constants.CATEGORY_COMPUTERS;

                break;
            case "أدوات كهربائيه":
                cat_id = Constants.CATEGORY_ELECTRICAl_TOOLS;

                break;
            case "إكسسوار":
                cat_id = Constants.CATEGORY_ACCESSORIES;

                break;
            case "ملابس":
                cat_id = Constants.CATEGORY_CLOTHES;

                break;
            case "أحذيه":
                cat_id = Constants.CATEGORY_SHOES;

                break;
        }
        return cat_id;
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
                case R.id.input_pricebefore:
                    validatePriceBefore();
                    break;
                case R.id.input_priceafter:
                    validatePriceAfter();
                    break;
            }
        }
    }

    private class MyImgurUploadTask extends ImgurUploadTask {
        int x;

        ImageView imageView, imageViewdone;

        public MyImgurUploadTask(Uri imageUri, ImageView imageView, ImageView imgdn, int y) {
            super(imageUri, getActivity());

            this.imageView = imageView;
            this.imageViewdone = imgdn;
            this.x = y - 1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mImgurUploadTask = this;
            mImgurUrl = null;

        }

        @Override
        protected void onPostExecute(String imageId) {
            super.onPostExecute(imageId);
            mImgurUploadTask = null;
            if (imageId != null) {
                mImgurUrl = "http://i.imgur.com/" + imageId + ".jpg";

                imageView.setAlpha(1.0f);

                imageViewdone.setVisibility(View.VISIBLE);

                Toast.makeText(getActivity(), mImgurUrl + "", Toast.LENGTH_LONG).show();

                images_id.add(mImgurUrl);


            } else {
                mImgurUrl = null;

                Toast.makeText(getActivity(), "imgur_upload_error", Toast.LENGTH_LONG).show();


            }

        }
    }
}

