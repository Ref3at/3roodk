package com.app3roodk.UI.Shop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import android.widget.TextView;

import com.app3roodk.Imgur.ImgurUploadTask;
import com.app3roodk.R;
import com.app3roodk.Schema.Offer;
import com.app3roodk.Schema.Shop;
import com.app3roodk.UI.DetailActivity.DetailActivity;
import com.app3roodk.UtilityFirebase;
import com.app3roodk.UtilityGeneral;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ViewShopFragment extends Fragment {

    static public LatLng latLngShop;
    static public LatLng latLngShop_Editing;
    Shop shop;
    List<Address> addresses;
    List<Address> addressesFromEditing;

    ArrayAdapter<String> adapter;
    ArrayList<String> lstOffersTitles;
    ArrayList<Offer> lstOffers;
    ExpandableHeightListView lvOffers;
    Boolean canEdit;

    InputMethodManager inputManager;

    TextView txtShopName, txtWorkingTime, AddressFromMap, txtAdressInfo, txtContacts, txtOffersNum, txtActiveOffersNum;
    EditText e_txtWorkingTime, e_txtAdressInfo, e_txtContacts;
    ImageView imageLogo;
    Button btnChangeLogo, btnShopWay;

    ImageButton btn_done_WorkingTime, btn_edit_WorkingTime, btn_done_AdressInfo,
            btn_edit_AdressInfo, btn_done_Contacts, btn_edit_Contacts, btn_done_logo, btn_edit_logo;

    int SELECT_FILE = 1, allOffersCounter;
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
        View rootView = inflater.inflate(R.layout.fragment_view_shop, container, false);

        if (getActivity().getIntent().getExtras() != null)
            shop = new Gson().fromJson(getActivity().getIntent().getStringExtra("shop"), Shop.class);
        else
            getActivity().finish();

        if (UtilityGeneral.loadShop(getActivity(), shop.getObjectId()) != null)
            canEdit = true;
        else
            canEdit = false;
        initOffersList(rootView);
        latLngShop = new LatLng(Double.parseDouble(shop.getLat()), Double.parseDouble(shop.getLon()));
        if (latLngShop_Editing == null)
            latLngShop_Editing = new LatLng(Double.parseDouble(shop.getLat()), Double.parseDouble(shop.getLon()));

        init(rootView);
        fillViews();
        btnsActions();

        return rootView;
    }

    private void initOffersList(View rootView) {
        lvOffers = (ExpandableHeightListView) rootView.findViewById(R.id.lvOffers);
        lstOffersTitles = new ArrayList<>();
        lstOffers = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, lstOffersTitles);
        lvOffers.setAdapter(adapter);
        lvOffers.setExpanded(true);
        allOffersCounter = 0;
        String[] cats = getResources().getStringArray(R.array.cat_array_eng);

        for (String cat : cats) {
            UtilityFirebase.getOffersForSpecificShop(shop,cat).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    Offer of;
                    double dateNow = Double.parseDouble(UtilityGeneral.getCurrentDate(new Date()));
                    while (iterator.hasNext()) {
                        of = iterator.next().getValue(Offer.class);
                        allOffersCounter++;
                        if (Double.parseDouble(of.getCreatedAt()) <= dateNow && Double.parseDouble(of.getEndTime()) > dateNow) {
                            lstOffers.add(of);
                            lstOffersTitles.add(of.getTitle());
                            adapter.notifyDataSetChanged();
                        }
                    }
                    txtActiveOffersNum.setText(String.valueOf(lstOffersTitles.size()));
                    txtOffersNum.setText(String.valueOf(allOffersCounter));
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
                UtilityFirebase.increaseOfferViewsNum(offer);
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
                                    UtilityFirebase.removeOffer(lstOffers.get(position),new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            UtilityFirebase.removeOfferExists(lstOffers.get(position), new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    UtilityFirebase.removeOfferComments(lstOffers.get(position).getObjectId());
                                                    lstOffersTitles.remove(position);
                                                    lstOffers.remove(position);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
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
                    showMessage("إدخل مواعيد العمل");
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
                    showMessage("إدخل تفاصيل عنوان المحل");
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
                    showMessage("إدخل رقم الموبايل");
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

        btnShopWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(UtilityGeneral.DrawPathToCertainShop(
                        getContext(), UtilityGeneral.getCurrentLonAndLat(getContext()), latLngShop));
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
                    addressesFromEditing = UtilityGeneral.getCurrentGovAndCityInEnglish(getActivity(), latLngShop_Editing);
                }
            }).start();
        } catch (Exception ex) {
        }
    }

    private void fillViews() {

        if (shop.getLogoId() == null) {
            Glide.with(getActivity()).load(R.drawable.logo).asBitmap().into(new BitmapImageViewTarget(imageLogo) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    imageLogo.setImageDrawable(circularBitmapDrawable);
                }
            });
        } else {
            Glide.with(getActivity()).load(shop.getLogoId()).asBitmap().into(new BitmapImageViewTarget(imageLogo) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    imageLogo.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
        txtShopName.setText(shop.getName());
        txtWorkingTime.setText(shop.getWorkingTime());
        txtAdressInfo.setText(shop.getAddress());
        txtContacts.setText(shop.getContacts());
    }

    private void init(View rootView) {
        // View Views
        imageLogo = (ImageView) rootView.findViewById(R.id.v_logo);
        txtShopName = (TextView) getActivity().findViewById(R.id.toolbarTitle);
        txtWorkingTime = (TextView) rootView.findViewById(R.id.v_workingtime);
        AddressFromMap = (TextView) rootView.findViewById(R.id.v_txtShopAddressFromMap);
        txtAdressInfo = (TextView) rootView.findViewById(R.id.v_addressinfo);
        txtContacts = (TextView) rootView.findViewById(R.id.v_contacts);
        txtOffersNum = (TextView) rootView.findViewById(R.id.txtOfferNum);
        txtActiveOffersNum = (TextView) rootView.findViewById(R.id.txtActiveOfferNum);
        btnChangeLogo = (Button) rootView.findViewById(R.id.btn_change_logo);
        btnShopWay = (Button) rootView.findViewById(R.id.btnShopWay);
        // Edit Views
        e_txtWorkingTime = (EditText) rootView.findViewById(R.id.E_workingtime);
        e_txtAdressInfo = (EditText) rootView.findViewById(R.id.E_addressinfo);
        e_txtContacts = (EditText) rootView.findViewById(R.id.E_contacts);
        btn_done_WorkingTime = (ImageButton) rootView.findViewById(R.id.btn_done_workingtime);
        btn_edit_WorkingTime = (ImageButton) rootView.findViewById(R.id.btn_edit_workingtime);
        btn_done_AdressInfo = (ImageButton) rootView.findViewById(R.id.btn_done_addressinfo);
        btn_edit_AdressInfo = (ImageButton) rootView.findViewById(R.id.btn_edit_addressinfo);
        btn_done_Contacts = (ImageButton) rootView.findViewById(R.id.btn_done_contacts);
        btn_edit_Contacts = (ImageButton) rootView.findViewById(R.id.btn_edit_contacts);
        btn_done_logo = (ImageButton) rootView.findViewById(R.id.btn_done_logo);
        btn_edit_logo = (ImageButton) rootView.findViewById(R.id.btn_edit_logo);
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
        btn_edit_WorkingTime.setVisibility(View.GONE);
        btn_edit_AdressInfo.setVisibility(View.GONE);
        btn_edit_Contacts.setVisibility(View.GONE);
        btn_edit_logo.setVisibility(View.GONE);
        btn_done_AdressInfo.setVisibility(View.GONE);
        btn_done_Contacts.setVisibility(View.GONE);
        btn_done_logo.setVisibility(View.GONE);
        btn_done_WorkingTime.setVisibility(View.GONE);
        btn_edit_logo.setVisibility(View.GONE);
        showMessage("جارى التحديث");
        UtilityFirebase.updateShop(UtilityGeneral.loadUser(getActivity()).getObjectId(),shop, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    showMessage("حصل مشكلة بص على النت كده");
                } else {
                    showMessage("تم التحديث بنجاح");
                    UtilityGeneral.saveShop(getActivity(), shop);
                }
            }
        });
    }

    private void editMode() {
        btn_edit_WorkingTime.setVisibility(View.VISIBLE);
        btn_edit_AdressInfo.setVisibility(View.VISIBLE);
        btn_edit_Contacts.setVisibility(View.VISIBLE);
        btn_edit_logo.setVisibility(View.VISIBLE);
    }

    private void showMessage(String msg){
        Snackbar.make(getView(),msg,Snackbar.LENGTH_LONG).show();
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
                showMessage("جاري رفع اللوجو");
                setLogo(data.getData());
            }
        }
    }
    private void setLogo( Uri uri) {

        Glide.with(getActivity()).load(uri).asBitmap().into(new BitmapImageViewTarget(imageLogo) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageLogo.setImageDrawable(circularBitmapDrawable);
            }
        });
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
                showMessage("تم رفع صورة اللوجو");
                mlogoId = mImgurUrl;
            } else {
                mImgurUrl = null;
                showMessage("فشل فى رفع صورة اللوجو");
            }
        }
    }

}
