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
import android.os.Build;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app3roodk.Constants;
import com.app3roodk.Imgur.ImgurUploadTask;
import com.app3roodk.R;
import com.app3roodk.Schema.Item;
import com.app3roodk.Schema.Offer;
import com.app3roodk.Schema.Shop;
import com.app3roodk.UtilityGeneral;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddNewOfferFragment extends Fragment {

  //  ArrayList<String> images_id = new ArrayList<>();
    ArrayList<String> [] images_id_Array = new ArrayList[3];




    ArrayList<Item> items_list = new ArrayList<>();
    ArrayList<LinearLayout> listOfContainers = new ArrayList<>();

    ArrayList<String> lstShopsString;
    ArrayList<Shop> lstShops;
    Offer mOffer;

    String[] durtation_list, cat_list;

//    int REQUEST_CAMERA = 0, SELECT_FILE = 1;


    ArrayList<Integer> REQUEST_CAMERA = new ArrayList<>();
    ArrayList<Integer> SELECT_FILE = new ArrayList<>();


    LinearLayout itemsContainer;
    BetterSpinner duration_spinner, category_spinner, shops_spinnner;
    private EditText inputName, inputDesc, inputPriceBefore, inputPriceAfter;
    private TextInputLayout inputLayoutName, inputLayoutDesc, inputLayoutPriceBefore, inputLayoutPriceAfter;
    private Button publishOffer, previewOffer, addNewItem;
    private String mImgurUrl;
    private MyImgurUploadTask mImgurUploadTask;


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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        images_id_Array [0]=  new ArrayList<>();
        images_id_Array [1]=  new ArrayList<>();
        images_id_Array [2]=  new ArrayList<>();


        REQUEST_CAMERA.add(55);
        REQUEST_CAMERA.add(66);
        REQUEST_CAMERA.add(77);

        SELECT_FILE.add(21);
        SELECT_FILE.add(28);
        SELECT_FILE.add(35);


        mOffer = new Offer();

        View rootView = inflater.inflate(R.layout.add_offer_layout, container, false);

        addNewItem = (Button) rootView.findViewById(R.id.add_new_item);

        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itemsContainer.getChildCount() > 2) {
                    Toast.makeText(getActivity(), "you need to delete another item", Toast.LENGTH_SHORT).show();
                } else {
                    inflateNewItem();
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
        for (Shop shop : lstShops)
            lstShopsString.add(shop.getName());
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, lstShopsString);
        shops_spinnner.setAdapter(adapter3);

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

    private void inflateNewItem() {


        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootView = inflater.inflate(R.layout.add_offer_item_layout, null);

        itemsContainer.addView(rootView);


        inputLayoutPriceBefore = (TextInputLayout) rootView.findViewById(R.id.input_layout_pricebefore);
        inputLayoutPriceAfter = (TextInputLayout) rootView.findViewById(R.id.input_layout_priceafter);
        inputPriceBefore = (EditText) rootView.findViewById(R.id.input_pricebefore);
        inputPriceAfter = (EditText) rootView.findViewById(R.id.input_priceafter);

        inputPriceBefore.addTextChangedListener(new MyTextWatcher(inputPriceBefore));
        inputPriceAfter.addTextChangedListener(new MyTextWatcher(inputPriceAfter));




        ImageButton delete_Button = (ImageButton) rootView.findViewById(R.id.delete_item);
        delete_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                images_id_Array[itemsContainer.indexOfChild(rootView)].clear();
                itemsContainer.removeView(rootView);



            }
        });




        ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.add_new_image);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout imgsContainer = (LinearLayout) rootView.findViewById(R.id.imgscontainer);

                if (imgsContainer.getChildCount() > 3) {
                    Toast.makeText(getActivity(), "you need to delete another images", Toast.LENGTH_SHORT).show();
                } else {
                    selectImage(itemsContainer.indexOfChild(rootView));
                }

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
        if (!validateShop()) {
            return;
        }

        prepareItems();

    }

    private void prepareItems() {

        for (int i = 0; i < itemsContainer.getChildCount(); i++) {


            RelativeLayout rootv = (RelativeLayout) itemsContainer.getChildAt(i);
            Item item = new Item();

            EditText  inputPriceBefore = (EditText) rootv.findViewById(R.id.input_pricebefore);
            EditText inputPriceAfter = (EditText) rootv.findViewById(R.id.input_priceafter);

            item.setPriceBefore(inputPriceBefore.getText().toString());
            item.setPriceAfter(inputPriceAfter.getText().toString());
            item.setImagePaths(images_id_Array[i]);


            items_list.add(item);
        }


        Publish();

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

        if (images_id_Array[0].size() < 1 && images_id_Array[1].size() < 1&& images_id_Array[2].size() < 1) {

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

    private Uri mFileUri = null;

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
                    startActivityForResult(intent, REQUEST_CAMERA.get(i));
                } else if (items[item].equals("إختار صوره")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "إختار صوره"),
                            SELECT_FILE.get(i));
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
            if (requestCode < 50) {
                Uri u = data.getData();
                    onSelectFromGalleryResult(data, u, requestCode);

            } else if (requestCode > 50) {
                    Uri u = data.getData();
                    onCaptureImageResult(data, u, requestCode);}

        }
    }

    private void onCaptureImageResult(Intent data, Uri uri, int requestCode) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);


        //  thumbnail.compress(Bitmap.CompressFormat.JPEG,40,bytes);

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

        inflateNewImage(thumbnail, uri, requestCode);
    }

    private void onSelectFromGalleryResult(Intent data, Uri uri, int requestCode) {
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

        inflateNewImage(bm, uri, requestCode);
    }

    private void inflateNewImage(Bitmap thumbnail, Uri uri, int requestCode) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootview = inflater.inflate(R.layout.image_group, null);
        //****************************
        int theIndex = 0;
        if (requestCode < 50) {
            theIndex = (requestCode / 7) - 3;
        } else {
            theIndex = (requestCode / 11) - 5;
        }
        LinearLayout imgsContainer = null;
if (itemsContainer!=null) {
     imgsContainer = (LinearLayout) itemsContainer.getChildAt(theIndex).findViewById(R.id.imgscontainer);

    imgsContainer.addView(rootview);
}
        final ImageView imageView = (ImageView) rootview.findViewById(R.id.theimage);
        imageView.setImageBitmap(thumbnail);
        imageView.setAlpha(0.5f);

        ImageView imgdone = (ImageView) rootview.findViewById(R.id.img_done);

        Toast.makeText(getActivity(), "uploading...", Toast.LENGTH_SHORT).show();

        new MyImgurUploadTask(uri, imageView, imgdone, theIndex).execute();

        ImageButton del_image = (ImageButton) rootview.findViewById(R.id.delete_img);
        final LinearLayout finalImgsContainer = imgsContainer;
        final int finalTheIndex = theIndex;
        del_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //**************************************************************************
                /// error here   the id come from asyns after we delete the image

                if (images_id_Array[finalTheIndex].size() > 0 && !images_id_Array[finalTheIndex].get(finalImgsContainer.indexOfChild(rootview)-1).isEmpty()) {
                    images_id_Array[finalTheIndex].remove(finalImgsContainer.indexOfChild(rootview)-1);
                }
                finalImgsContainer.removeView(rootview);
            }
        });
    }

    private void Publish() {
        try {
            mOffer.setCategoryName(UtilityGeneral.getCategoryEnglishName(category_spinner.getText().toString()));
            mOffer.setTitle(inputName.getText().toString());
            mOffer.setDesc(inputDesc.getText().toString());

            //    mOffer.setPriceBefore(inputPriceBefore.getText().toString());
            //    mOffer.setPriceAfter(inputPriceAfter.getText().toString());
            mOffer.setPeriod(duration_spinner.getText().toString());
            //  mOffer.setImagePaths(images_id);

            // new
            mOffer.setItems(items_list);
            Shop shop = null;
            for (Shop sh : lstShops)
                if (sh.getName().equals(shops_spinnner.getText().toString())) {
                    shop = sh;
                    break;
                }

            mOffer.setShopId(shop.getObjectId());
            mOffer.setShopName(shop.getName());
            mOffer.setLat(shop.getLat());
            mOffer.setLon(shop.getLon());
            mOffer.setCreatedAt(UtilityGeneral.getCurrentDate(new Date()));
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
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Offers").child(shop.getCity()).child(UtilityGeneral.getCategoryEnglishName(category_spinner.getText().toString()));
            mOffer.setObjectId(myRef.push().getKey());
            myRef.child(mOffer.getObjectId()).setValue(mOffer, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toast.makeText(getActivity(), "حصل مشكله شوف النت ", Toast.LENGTH_SHORT).show();
                        Log.e("Frebaaase", databaseError.getMessage());
                    } else {
                        Toast.makeText(getActivity(), "تم إضافه العرض، شكرا لك", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    }
                }
            });
        } catch (Exception ex) {
            Log.e("AddNewOfferFrag", ex.getMessage());
        }

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
            case "أدوات منزليه":
                cat_id = Constants.CATEGORY_HOME_TOOLS;

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
            case "خدمات":
                cat_id = Constants.CATEGORY_SERVICES;

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
            this.x = y ;
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


                images_id_Array[x].add(mImgurUrl);


            } else {
                mImgurUrl = null;

                Toast.makeText(getActivity(), "imgur_upload_error", Toast.LENGTH_LONG).show();


            }

        }
    }
}