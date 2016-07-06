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
import android.os.DropBoxManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

import com.app3roodk.Imgur.ImgurUploadTask;
import com.app3roodk.R;
import com.app3roodk.Schema.Item;
import com.app3roodk.Schema.Offer;
import com.app3roodk.Schema.Shop;
import com.app3roodk.UtilityFirebase;
import com.app3roodk.UtilityGeneral;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddNewOfferFragment extends Fragment {

    ArrayList<String>[] images_id_Array = new ArrayList[3];

    ArrayList<Item> items_list = new ArrayList<>();

    ArrayList<String> lstShopsString;
    ArrayList<Shop> lstShops;
    Offer mOffer;
    HashMap<String, MyImgurUploadTask> mapImageUploading;

    String[] durtation_list, cat_list;

    ArrayList<Integer> REQUEST_CAMERA = new ArrayList<>();
    ArrayList<Integer> SELECT_FILE = new ArrayList<>();


    LinearLayout itemsContainer;
    BetterSpinner duration_spinner, category_spinner, shops_spinnner;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        images_id_Array[0] = new ArrayList<>();
        images_id_Array[1] = new ArrayList<>();
        images_id_Array[2] = new ArrayList<>();

        REQUEST_CAMERA.add(55);
        REQUEST_CAMERA.add(66);
        REQUEST_CAMERA.add(77);

        SELECT_FILE.add(21);
        SELECT_FILE.add(28);
        SELECT_FILE.add(35);

        mOffer = new Offer();
        mapImageUploading = new HashMap<>();

        View rootView = inflater.inflate(R.layout.fragment_add_new_offer, container, false);
        addNewItem = (Button) rootView.findViewById(R.id.add_new_item);
        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inflateNewItem(itemsContainer.getChildCount()==0);
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

    private void inflateNewItem(boolean firstOne) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootView = inflater.inflate(R.layout.add_offer_item, null);
        itemsContainer.addView(rootView);
        if (firstOne)
            rootView.findViewById(R.id.delete_item).setVisibility(View.GONE);
        else
            rootView.findViewById(R.id.delete_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    images_id_Array[itemsContainer.indexOfChild(rootView)].clear();
                    itemsContainer.removeView(rootView);
                    addNewItem.setVisibility(View.VISIBLE);
                }
            });

        rootView.findViewById(R.id.add_new_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout imgsContainer = (LinearLayout) rootView.findViewById(R.id.imgscontainer);
                selectImage(itemsContainer.indexOfChild(rootView));
                if (imgsContainer.getChildCount() == 3) {
                    rootView.findViewById(R.id.add_new_image).setVisibility(View.GONE);
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
        for (int i = 0; i < itemsContainer.getChildCount(); i++) {
            RelativeLayout rootView = (RelativeLayout) itemsContainer.getChildAt(i);
            Item item = new Item();
            EditText inputPriceBefore = (EditText) rootView.findViewById(R.id.input_pricebefore);
            EditText inputPriceAfter = (EditText) rootView.findViewById(R.id.input_priceafter);
            item.setPriceBefore(inputPriceBefore.getText().toString());
            item.setPriceAfter(inputPriceAfter.getText().toString());
            item.setImagePaths(images_id_Array[i]);
            if (item.getPriceAfter().isEmpty()) {
                ((TextInputLayout) rootView.findViewById(R.id.input_layout_priceafter)).setError(getString(R.string.err_msg_priceBefore));
                requestFocus(inputPriceBefore);
                success = false;
            } else {
                ((TextInputLayout) rootView.findViewById(R.id.input_layout_priceafter)).setErrorEnabled(false);
            }
            if (item.getPriceBefore().isEmpty()) {
                ((TextInputLayout) rootView.findViewById(R.id.input_layout_pricebefore)).setError(getString(R.string.err_msg_priceBefore));
                requestFocus(inputPriceBefore);
                success = false;
            } else {
                ((TextInputLayout) rootView.findViewById(R.id.input_layout_pricebefore)).setErrorEnabled(false);
            }

            items_list.add(item);
        }
        if (!success) return;
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

    private Boolean validateImagesIDs() {
        for (Map.Entry<String, MyImgurUploadTask> entry : mapImageUploading.entrySet()) {
            if (!entry.getValue().success) {
                showMessage("يجب الإنتظار حتى يتم رفع كل الصور");
                return false;
            }
        }
        if (images_id_Array[0].size() < 1 && images_id_Array[1].size() < 1 && images_id_Array[2].size() < 1) {

            showMessage("يجب إضافة صورة على الأقل لكل عرض");
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
            inflateNewImage(data.getData(), requestCode);
        }
    }

    private void inflateNewImage(Uri uri, int requestCode) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootImage = inflater.inflate(R.layout.image_group, null);
        final String key = UUID.randomUUID().toString();
        int indexOfItem;
        if (requestCode < 50) {
            indexOfItem = (requestCode / 7) - 3;
        } else {
            indexOfItem = (requestCode / 11) - 5;
        }
        LinearLayout lytImagesGroupContainer = null;
        if (itemsContainer != null) {
            lytImagesGroupContainer = (LinearLayout) itemsContainer.getChildAt(indexOfItem).findViewById(R.id.imgscontainer);
            lytImagesGroupContainer.addView(rootImage);
        }
        final ImageView imgOffer = (ImageView) rootImage.findViewById(R.id.theimage);
        Glide.with(getActivity()).load(uri).into(imgOffer);
        imgOffer.setAlpha(0.5f);
        ImageView imgDone = (ImageView) rootImage.findViewById(R.id.img_done);
        showMessage("جاري رفع الصورة");
        mapImageUploading.put(key, new MyImgurUploadTask(uri, imgOffer, imgDone, indexOfItem));
        mapImageUploading.get(key).execute();
        final LinearLayout fLytImagesGroupContainer = lytImagesGroupContainer;
        final int fIndexOfItem = indexOfItem;
        rootImage.findViewById(R.id.delete_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mapImageUploading.get(key).isDone())
                    mapImageUploading.get(key).cancel(true);
                else
                    images_id_Array[fIndexOfItem].remove(fLytImagesGroupContainer.indexOfChild(rootImage) - 1);
                mapImageUploading.remove(key);
                fLytImagesGroupContainer.findViewById(R.id.add_new_image).setVisibility(View.VISIBLE);
                fLytImagesGroupContainer.removeView(rootImage);
            }
        });
    }

    private void showMessage(String msg) {
        try {
            Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).show();
        } catch (Exception ex) {
        }

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
            mOffer.setShopId(shop.getObjectId());
            mOffer.setShopName(shop.getName());
            mOffer.setLat(shop.getLat());
            mOffer.setLon(shop.getLon());
            mOffer.setCreatedAt(UtilityGeneral.getCurrentDate(new Date()));
            mOffer.setAverageRate("0");
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
            UtilityFirebase.createNewOffer(mOffer, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        showMessage("حصل مشكله شوف النت ");
                        Log.e("NewOfferFailed", databaseError.getMessage());
                    } else {
                        showMessage("تم إضافه العرض، شكرا لك");
                        getActivity().onBackPressed();
                    }
                }
            });
        } catch (Exception ex) {
            Log.e("AddNewOfferFrag", ex.getMessage());
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

    private class MyImgurUploadTask extends ImgurUploadTask {
        int x;
        boolean done, success;
        ImageView imageView, imageViewdone;

        public boolean isDone() {
            return done;
        }

        public MyImgurUploadTask(Uri imageUri, ImageView imageView, ImageView imgdn, int y) {
            super(imageUri, getActivity());
            this.imageView = imageView;
            this.imageViewdone = imgdn;
            this.x = y;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mImgurUrl = null;
            success = false;
            done = false;
        }

        @Override
        protected void onPostExecute(String imageId) {
            super.onPostExecute(imageId);

            if (imageId != null) {
                mImgurUrl = "http://i.imgur.com/" + imageId + ".jpg";
                imageView.setAlpha(1.0f);
                imageViewdone.setVisibility(View.VISIBLE);
                showMessage("تم رفع الصورة");
                images_id_Array[x].add(mImgurUrl);
                done = true;
                success = true;
            } else {
                mImgurUrl = null;
                done = true;
                success = false;
                showMessage("حدث خطأ أثناء رفع الصورة");
            }
        }
    }
}