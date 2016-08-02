package com.app3roodk.UI.Shop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app3roodk.R;
import com.app3roodk.Schema.Shop;
import com.app3roodk.UtilityGeneral;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ListShopsFragment extends Fragment {

    FloatingActionButton btnAdd;
    ListView lvShops;
    ArrayAdapter<String> adapter;
    ArrayList<String> lstShopsString;
    ArrayList<Shop> lstShops;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_shops, container, false);
        init(rootView);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), ShopActivity.class), 1);
            }
        });

        lvShops.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ViewShopActivity.class);
                intent.putExtra("shop", new Gson().toJson(lstShops.get(position)));
                startActivityForResult(intent, 1);
            }
        });

        return rootView;
    }

    private void init(View rootView) {
        lvShops = (ListView) rootView.findViewById(R.id.lstShops);
        btnAdd = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        btnAdd.attachToListView(lvShops);


        lstShopsString = new ArrayList<>();

        fillShops();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fillShops();
    }

    private void fillShops() {
        lstShops = UtilityGeneral.loadShopsList(getActivity());
        ShopAdapter shopAdapter = new ShopAdapter(getActivity(), lstShops);
        lvShops.setAdapter(shopAdapter);
        shopAdapter.notifyDataSetChanged();
    }


    class ShopAdapter extends ArrayAdapter {

        public ShopAdapter(Context context, List<Shop> shop_lst) {
            super(context, 0, shop_lst);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
            final Shop shop = (Shop) getItem(position);


            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.shop_list_view_item, parent, false);
            }
            final Button del, active;
            final TextView inactive, shopName;

            del = (Button) convertView.findViewById(R.id.btn_delete_shop);
            active = (Button) convertView.findViewById(R.id.btn_activate);
            inactive = (TextView) convertView.findViewById(R.id.txt_inactive);
            shopName = (TextView) convertView.findViewById(R.id.txt_shop_name);

            shopName.setText(shop.getName().toString());
            if (!shop.isShopActive()) {
                inactive.setVisibility(View.VISIBLE);
                active.setVisibility(View.VISIBLE);
            }
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(getContext(), "انتيه سوف يتم مسح المحل وكل العروض المتعلقه بيه ها!", Toast.LENGTH_SHORT).show();

                }
            });

            active.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    // Get the layout inflater
                    LayoutInflater inflater = getActivity().getLayoutInflater();

                    final View viewinflater = inflater.inflate(R.layout.code_dialog_layout, null);

                    builder.setView(viewinflater);
                    final AlertDialog alert = builder.create();
                    alert.show();

                    final EditText etxtCode = (EditText) viewinflater.findViewById(R.id.etxt_code);
                    etxtCode.setText("");

                    Button buttonCall = (Button) viewinflater.findViewById(R.id.btn_call);
                    buttonCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "01001349907"));
                            getContext().startActivity(intent);


                        }
                    });


                    Button buttonCancel = (Button) viewinflater.findViewById(R.id.btn_dialog_cancel);
                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();

                        }
                    });


                    Button buttonDone = (Button) viewinflater.findViewById(R.id.btn_dialog_done);
                    buttonDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (etxtCode.getText().toString().length() == 0) {
                                Toast.makeText(getContext(), "من فضلك أدخل الكود أولاً!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (UtilityGeneral.isCodeValid(String.valueOf(etxtCode.getText().toString()), shop.getPinCode().toString())) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("Shops")
                                        .child(UtilityGeneral.loadUser(getContext()).getObjectId())
                                        .child(shop.getObjectId())
                                        .child("shopActive");

                                myRef.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "تم تفعيل المحل شكراً لك!", Toast.LENGTH_SHORT).show();
                                            active.setVisibility(View.GONE);
                                            inactive.setVisibility(View.GONE);
                                            alert.dismiss();
                                            shop.setShopActive(true);
                                            UtilityGeneral.saveShop(getContext(), shop);
                                            //    getActivity().finish();
                                            //  startActivity(getActivity().getIntent());


                                        } else {
                                            Toast.makeText(getContext(), "لديك مشكله فى اتصال الانترنت، يرجى إعاده المحاوله", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            } else {
                                Toast.makeText(getContext(), "من فضلك أدخل كود صحيح وأعد المحاوله مره أخرى!", Toast.LENGTH_SHORT).show();

                            }


                        }
                    });


                }
            });

            return convertView;
        }
    }

}