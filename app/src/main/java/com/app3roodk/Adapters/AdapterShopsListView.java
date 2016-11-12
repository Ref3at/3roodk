package com.app3roodk.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.app3roodk.Holders.HolderShopsCodeBuilder;
import com.app3roodk.Holders.HolderShopsListView;
import com.app3roodk.R;
import com.app3roodk.Schema.Shop;
import com.app3roodk.UtilityGeneral;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterShopsListView extends ArrayAdapter {
    private Activity mActivity;
    private ArrayList<Shop> mData;
    private int mResourceId;

    public AdapterShopsListView(Activity activity, ArrayList<Shop> data) {
        super(activity.getBaseContext(), R.layout.shop_list_view_item, data);
        mResourceId = R.layout.shop_list_view_item;
        mActivity = activity;
        mData = data;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final HolderShopsListView holder;
        if (row == null) {
            LayoutInflater inflater = (mActivity).getLayoutInflater();
            row = inflater.inflate(mResourceId, parent, false);
            holder = new HolderShopsListView(row, mActivity.getBaseContext());
            row.setTag(holder);
        } else {
            holder = (HolderShopsListView) row.getTag();
        }
        holder.setShopName(mData.get(position).getName());
        holder.setShopActive(mData.get(position).isShopActive());
        holder.setDeleteClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "انتيه سوف يتم مسح المحل وكل العروض المتعلقه بيه ها!", Toast.LENGTH_SHORT).show();
            }
        });
        holder.setActiveClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                LayoutInflater inflater = mActivity.getLayoutInflater();
                final View viewInflater = inflater.inflate(R.layout.code_dialog_layout, null);
                builder.setView(viewInflater);
                final AlertDialog alert = builder.create();
                alert.show();
                final HolderShopsCodeBuilder holderBuilder = new HolderShopsCodeBuilder(viewInflater, mActivity, alert);
                holderBuilder.setDoneClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holderBuilder.isCodeEmpty()) {
                            Toast.makeText(getContext(), "من فضلك أدخل الكود أولاً!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (UtilityGeneral.isCodeValid(holderBuilder.getCode(), mData.get(position).getPinCode())) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Shops")
                                    .child(UtilityGeneral.loadUser(getContext()).getObjectId())
                                    .child(mData.get(position).getObjectId())
                                    .child("shopActive");

                            myRef.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "تم تفعيل المحل شكراً لك!", Toast.LENGTH_SHORT).show();
                                        alert.dismiss();
                                        mData.get(position).setShopActive(true);
                                        holder.setShopActive(mData.get(position).isShopActive());
                                        UtilityGeneral.saveShop(getContext(), mData.get(position));
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
        return row;
    }
}
