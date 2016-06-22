package com.app3roodk.UI.Shop;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app3roodk.Imgur.ImgurUploadTask;
import com.app3roodk.MapsShopLocationActivity;
import com.app3roodk.R;
import com.app3roodk.Schema.Offer;
import com.app3roodk.Schema.Shop;
import com.app3roodk.UI.DetailActivity.DetailActivity;
import com.app3roodk.UtilityGeneral;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.pool.PoolStats;

/**
 * Created by Refaat on 5/6/2016.
 */
public class ViewShopFragment extends Fragment {

    static public LatLng latLngShop;
    static public LatLng latLngShop_Editing;
    Shop shop;
    List<Address> addresses;
    List<Address> addressesFromEditing;

    ArrayAdapter<String> adapter;
    ArrayList<String> lstOffersTitles;
    ArrayList<Offer> lstOffers;
    ListView lvOffers;
    Boolean canEdit;

    InputMethodManager inputManager;

    LinearLayout lineaAdderss, lineaAdderssFromEditing;


    TextView txtShopName, txtWorkingTime, AddressFromMap, AddressFromMapByEditing, txtAdressInfo, txtContacts;
    EditText e_txtShopName, e_txtWorkingTime, e_txtAdressInfo, e_txtContacts;
    ImageView imageLogo;
    Button btnShopWay, btnChangeLocation, btnChangeLogo;

    ImageButton btn_done_ShopName, btn_edit_ShopName;
    ImageButton btn_done_WorkingTime, btn_edit_WorkingTime;
    ImageButton btn_done_AdressInfo, btn_edit_AdressInfo;
    ImageButton btn_done_Contacts, btn_edit_Contacts;
    ImageButton btn_done_logo, btn_edit_logo;

    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String mlogoId = null;
    Menu mMenu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_shop_layout, container, false);

        if (getActivity().getIntent().getExtras() != null) {
            shop = new Gson().fromJson(getActivity().getIntent().getStringExtra("shop"), Shop.class);
        } else {
            getActivity().finish();
        }

        if (UtilityGeneral.loadShop(getActivity(), shop.getObjectId()) != null) {
            Toast.makeText(getActivity(), "انت صاحب المحل", Toast.LENGTH_SHORT).show();

            canEdit = true;
        } else {
            Toast.makeText(getActivity(), "مش انت صاحب المحل", Toast.LENGTH_SHORT).show();

            canEdit = false;
        }
        initOffersList(rootView);
        latLngShop = new LatLng(Double.parseDouble(shop.getLat()), Double.parseDouble(shop.getLon()));

        if (latLngShop_Editing == null) {
            latLngShop_Editing = new LatLng(Double.parseDouble(shop.getLat()), Double.parseDouble(shop.getLon()));
        }
        init(rootView);
        fillViews();
        btnsActions();


        btnShopWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(UtilityGeneral.DrawPathToCertainShop(
                        getContext(), UtilityGeneral.getCurrentLonAndLat(getContext()),
                        latLngShop));
            }
        });

        btnChangeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MapsShopLocationActivity.class));
            }
        });

        return rootView;
    }

    private void initOffersList(View rootView) {
        lvOffers = (ListView) rootView.findViewById(R.id.lvOffers);
        lstOffersTitles = new ArrayList<>();
        lstOffers = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, lstOffersTitles);
        lvOffers.setAdapter(adapter);
        String[] cats = getResources().getStringArray(R.array.cat_array_eng);
        for (String cat : cats) {
            FirebaseDatabase.getInstance().getReference("Offers").child(shop.getCity()).child(cat)
                    .orderByChild("shopId").startAt(shop.getObjectId()).endAt(shop.getObjectId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    Offer of;
                    double dateNow = Double.parseDouble(UtilityGeneral.getCurrentDate(new Date()));
                    while (iterator.hasNext()) {
                        of = iterator.next().getValue(Offer.class);
                        if (Double.parseDouble(of.getCreatedAt()) <= dateNow && Double.parseDouble(of.getEndTime()) > dateNow) {
                            lstOffers.add(of);
                            lstOffersTitles.add(of.getTitle());
                            adapter.notifyDataSetChanged();
                            UtilityGeneral.setListViewHeightBasedOnChildren(lvOffers);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        lvOffers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Offer offer = lstOffers.get(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                FirebaseDatabase.getInstance().getReference("Offers").child(offer.getCity()).child(offer.getCategoryName()).child(offer.getObjectId()).child("viewNum").setValue(offer.getViewNum() + 1);
                offer.setViewNum(offer.getViewNum() + 1);
                intent.putExtra("offer", new Gson().toJson(offer));
                startActivity(intent);
            }
        });
        if (canEdit) {
            lvOffers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    FirebaseDatabase.getInstance().getReference("Offers").child(shop.getCity()).child(lstOffers.get(position).getCategoryName()).child(lstOffers.get(position).getObjectId()).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            lstOffersTitles.remove(position);
                                            lstOffers.remove(position);
                                            adapter.notifyDataSetChanged();
                                            UtilityGeneral.setListViewHeightBasedOnChildren(lvOffers);
                                        }
                                    });
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("انت عايز تمسح " + lstOffersTitles.get(position)).setPositiveButton("ايوه", dialogClickListener)
                            .setNegativeButton("لا", dialogClickListener).show();
                    return true;
                }
            });
        }
    }

    private void btnsActions() {

        btn_edit_ShopName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_edit_ShopName.setVisibility(View.GONE);
                btn_done_ShopName.setVisibility(View.VISIBLE);
                e_txtShopName.setText(txtShopName.getText().toString());
                e_txtShopName.requestFocus();
                txtShopName.setVisibility(View.GONE);
                e_txtShopName.setVisibility(View.VISIBLE);

            }
        });

        btn_done_ShopName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (e_txtShopName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), "ادخل اسم صحيح", Toast.LENGTH_SHORT).show();
                    return;
                }
                btn_done_ShopName.setVisibility(View.GONE);
                btn_edit_ShopName.setVisibility(View.VISIBLE);
                txtShopName.setText(e_txtShopName.getText().toString());
                txtShopName.setVisibility(View.VISIBLE);

                shop.setName(txtShopName.getText().toString());


                inputManager.hideSoftInputFromWindow(e_txtShopName.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                e_txtShopName.setVisibility(View.GONE);

            }
        });


        btn_edit_WorkingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_edit_WorkingTime.setVisibility(View.GONE);
                btn_done_WorkingTime.setVisibility(View.VISIBLE);
                e_txtWorkingTime.setText(txtWorkingTime.getText().toString());
                e_txtWorkingTime.requestFocus();

                txtWorkingTime.setVisibility(View.GONE);
                e_txtWorkingTime.setVisibility(View.VISIBLE);

            }
        });

        btn_done_WorkingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (e_txtWorkingTime.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), "ادخل مواعيد صحيحه", Toast.LENGTH_SHORT).show();
                    return;
                }


                btn_done_WorkingTime.setVisibility(View.GONE);
                btn_edit_WorkingTime.setVisibility(View.VISIBLE);
                txtWorkingTime.setText(e_txtWorkingTime.getText().toString());
                txtWorkingTime.setVisibility(View.VISIBLE);

                shop.setWorkingTime(txtWorkingTime.getText().toString());


                inputManager.hideSoftInputFromWindow(e_txtWorkingTime.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                e_txtWorkingTime.setVisibility(View.GONE);


            }
        });

        btn_edit_AdressInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_edit_AdressInfo.setVisibility(View.GONE);
                btn_done_AdressInfo.setVisibility(View.VISIBLE);
                e_txtAdressInfo.setText(txtAdressInfo.getText().toString());
                e_txtAdressInfo.requestFocus();

                txtAdressInfo.setVisibility(View.GONE);
                e_txtAdressInfo.setVisibility(View.VISIBLE);

            }
        });

        btn_done_AdressInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (e_txtAdressInfo.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), "ادخل عنوان صحيح", Toast.LENGTH_SHORT).show();
                    return;
                }
                btn_done_AdressInfo.setVisibility(View.GONE);
                btn_edit_AdressInfo.setVisibility(View.VISIBLE);
                txtAdressInfo.setText(e_txtAdressInfo.getText().toString());
                txtAdressInfo.setVisibility(View.VISIBLE);
                shop.setAddress(txtAdressInfo.getText().toString());


                inputManager.hideSoftInputFromWindow(e_txtAdressInfo.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                e_txtAdressInfo.setVisibility(View.GONE);

            }
        });

        btn_edit_Contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_edit_Contacts.setVisibility(View.GONE);
                btn_done_Contacts.setVisibility(View.VISIBLE);
                e_txtContacts.setText(txtContacts.getText().toString());
                e_txtContacts.requestFocus();

                txtContacts.setVisibility(View.GONE);
                e_txtContacts.setVisibility(View.VISIBLE);

            }
        });

        btn_done_Contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (e_txtContacts.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getActivity(), "ادخل رقم صحيح لموبايلك", Toast.LENGTH_SHORT).show();
                    return;
                }

                btn_done_Contacts.setVisibility(View.GONE);
                btn_edit_Contacts.setVisibility(View.VISIBLE);
                txtContacts.setText(e_txtContacts.getText().toString());
                txtContacts.setVisibility(View.VISIBLE);

                shop.setContacts(txtContacts.getText().toString());

                inputManager.hideSoftInputFromWindow(e_txtContacts.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                e_txtContacts.setVisibility(View.GONE);


            }
        });

        btn_edit_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_edit_logo.setVisibility(View.GONE);
                btnChangeLogo.setVisibility(View.VISIBLE);
                btn_done_logo.setVisibility(View.VISIBLE);
            }
        });

        btn_done_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_done_logo.setVisibility(View.GONE);
                btnChangeLogo.setVisibility(View.GONE);
                btn_edit_logo.setVisibility(View.VISIBLE);

                if (mlogoId != null) {
                    shop.setLogoId(mlogoId);
                }
            }
        });


        btnChangeLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLogo();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        writeAddress();
        writeAddressByEditing();
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
                            }
                        }
                    });
                    addresses = UtilityGeneral.getCurrentGovAndCityInEnglish(getActivity(), latLngShop);
                }
            }).start();
        } catch (Exception ex) {
        }
    }

    private void writeAddressByEditing() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<Address> tempAddresses = UtilityGeneral.getCurrentGovAndCity(getActivity(), latLngShop_Editing);
                    if (getActivity() == null)
                        return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                AddressFromMapByEditing.setText(tempAddresses.get(0).getAddressLine(3) + " - " + tempAddresses.get(0).getAddressLine(2) + " - " + tempAddresses.get(0).getAddressLine(1) + " - " + tempAddresses.get(0).getAddressLine(0));
                            } catch (Exception ex) {
                                Log.e("CreateShopFragment", ex.getMessage());
                            }
                        }
                    });
                    addressesFromEditing = UtilityGeneral.getCurrentGovAndCityInEnglish(getActivity(), latLngShop_Editing);
                }
            }).start();
        } catch (Exception ex) {
        }
    }

    private void fillViews() {

        if (shop.getLogoId() == null) {
            imageLogo.setVisibility(View.GONE);
        } else {
            Picasso.with(getActivity()).load(shop.getLogoId()).into(imageLogo);
        }


        txtShopName.setText(shop.getName());
        txtWorkingTime.setText(shop.getWorkingTime());
        txtAdressInfo.setText(shop.getAddress());
        txtContacts.setText(shop.getContacts());

    }

    private void init(View rootView) {

        // View Views

        imageLogo = (ImageView) rootView.findViewById(R.id.v_logo);
        txtShopName = (TextView) rootView.findViewById(R.id.v_shopname);
        txtWorkingTime = (TextView) rootView.findViewById(R.id.v_workingtime);
        AddressFromMap = (TextView) rootView.findViewById(R.id.v_txtShopAddressFromMap);
        AddressFromMapByEditing = (TextView) rootView.findViewById(R.id.txtShopAddressFromMapByEditing);
        txtAdressInfo = (TextView) rootView.findViewById(R.id.v_addressinfo);
        txtContacts = (TextView) rootView.findViewById(R.id.v_contacts);
        btnShopWay = (Button) rootView.findViewById(R.id.v_btnShopWay);
        btnChangeLocation = (Button) rootView.findViewById(R.id.btnChangeAddress);
        btnChangeLogo = (Button) rootView.findViewById(R.id.btn_change_logo);


        // Edit Views

        e_txtShopName = (EditText) rootView.findViewById(R.id.E_shopname);

        e_txtWorkingTime = (EditText) rootView.findViewById(R.id.E_workingtime);

        e_txtAdressInfo = (EditText) rootView.findViewById(R.id.E_addressinfo);

        e_txtContacts = (EditText) rootView.findViewById(R.id.E_contacts);

        btn_done_ShopName = (ImageButton) rootView.findViewById(R.id.btn_done_shopname);
        btn_edit_ShopName = (ImageButton) rootView.findViewById(R.id.btn_edit_shopname);

        btn_done_WorkingTime = (ImageButton) rootView.findViewById(R.id.btn_done_workingtime);
        btn_edit_WorkingTime = (ImageButton) rootView.findViewById(R.id.btn_edit_workingtime);

        btn_done_AdressInfo = (ImageButton) rootView.findViewById(R.id.btn_done_addressinfo);
        btn_edit_AdressInfo = (ImageButton) rootView.findViewById(R.id.btn_edit_addressinfo);

        btn_done_Contacts = (ImageButton) rootView.findViewById(R.id.btn_done_contacts);
        btn_edit_Contacts = (ImageButton) rootView.findViewById(R.id.btn_edit_contacts);


        btn_done_logo = (ImageButton) rootView.findViewById(R.id.btn_done_logo);
        btn_edit_logo = (ImageButton) rootView.findViewById(R.id.btn_edit_logo);


        lineaAdderss = (LinearLayout) rootView.findViewById(R.id.linear_address);
        lineaAdderssFromEditing = (LinearLayout) rootView.findViewById(R.id.linear_address_fromEditing);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_shop_menu, menu);
        MenuItem menuItemEdit = menu.findItem(R.id.action_edit);
        menuItemEdit.setVisible(canEdit);
        mMenu = menu;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        MenuItem itemDone = mMenu.findItem(R.id.action_done);
        MenuItem itemEdit = mMenu.findItem(R.id.action_edit);

        int item_id = item.getItemId();
        switch (item_id) {
            case R.id.action_edit:

                item.setVisible(false);
                itemDone.setVisible(true);

                editMode();

                return true;

            case R.id.action_done:

                item.setVisible(false);
                itemEdit.setVisible(true);

                doneMode();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doneMode() {

        lineaAdderss.setVisibility(View.VISIBLE);
        lineaAdderssFromEditing.setVisibility(View.GONE);


        btn_edit_ShopName.setVisibility(View.GONE);
        btn_edit_WorkingTime.setVisibility(View.GONE);
        btn_edit_AdressInfo.setVisibility(View.GONE);
        btn_edit_Contacts.setVisibility(View.GONE);
        btn_edit_logo.setVisibility(View.GONE);


        Toast.makeText(getActivity(), "done mode", Toast.LENGTH_SHORT).show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Shops")
                .child(UtilityGeneral.loadUser(getActivity()).getObjectId());


        myRef.child(shop.getObjectId()).setValue(shop, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(getActivity(), "حصل مشكله شوف النت ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "تم إضافه تعديل بيانات المحل شكرا لك", Toast.LENGTH_SHORT).show();
                    UtilityGeneral.saveShop(getActivity(), shop);

                }
            }
        });

    }


    private void editMode() {


        btn_edit_ShopName.setVisibility(View.VISIBLE);
        btn_edit_WorkingTime.setVisibility(View.VISIBLE);
        btn_edit_AdressInfo.setVisibility(View.VISIBLE);
        btn_edit_Contacts.setVisibility(View.VISIBLE);
        lineaAdderss.setVisibility(View.GONE);
        lineaAdderssFromEditing.setVisibility(View.VISIBLE);
        btn_edit_logo.setVisibility(View.VISIBLE);


        Toast.makeText(getActivity(), "edit mode", Toast.LENGTH_SHORT).show();


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

        imageLogo.setImageBitmap(thumbnail);
        imageLogo.setAlpha(0.4f);
        new MyImgurUploadTask(uri, imageLogo).execute();
    }

    private class MyImgurUploadTask extends ImgurUploadTask {
        String mImgurUrl = null;
        ImageView imageButton;

        public MyImgurUploadTask(Uri imageUri, ImageView addShopLogo) {
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
                imageLogo.setAlpha(1.0f);
                Toast.makeText(getActivity(), mImgurUrl + "", Toast.LENGTH_LONG).show();
                mlogoId = mImgurUrl;
            } else {
                mImgurUrl = null;
                Toast.makeText(getActivity(), "imgur_upload_error", Toast.LENGTH_LONG).show();
            }
        }
    }


}
