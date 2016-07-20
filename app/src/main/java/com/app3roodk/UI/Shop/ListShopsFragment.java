package com.app3roodk.UI.Shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.app3roodk.R;
import com.app3roodk.Schema.Shop;
import com.app3roodk.UtilityGeneral;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

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
        clickConfig();
        return rootView;
    }

    private void init(View rootView) {
        lvShops = (ListView) rootView.findViewById(R.id.lstShops);
        btnAdd = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        btnAdd.attachToListView(lvShops);


        lstShopsString = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, lstShopsString);
        lvShops.setAdapter(adapter);
        fillShops();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fillShops();
    }

    private void fillShops() {
        lstShopsString.clear();
        lstShops = UtilityGeneral.loadShopsList(getActivity());
        for (Shop shop : lstShops)
            lstShopsString.add(shop.getName());
        adapter.notifyDataSetChanged();
    }

    private void clickConfig() {
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
    }
}
