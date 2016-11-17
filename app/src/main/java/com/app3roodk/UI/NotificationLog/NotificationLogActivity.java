package com.app3roodk.UI.NotificationLog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app3roodk.Constants;
import com.app3roodk.R;
import com.app3roodk.Schema.CustomNotification;
import com.app3roodk.UI.DetailActivity.DetailActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class NotificationLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_log);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));


//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("REFAATPREFS2", MODE_PRIVATE);
        String notifJson = prefs.getString("REFAATPREFS2", "not found");
        Type typee = new TypeToken<ArrayList<CustomNotification>>() {
        }.getType();
        ArrayList<CustomNotification> customNotifList = new Gson().fromJson(notifJson, typee);


//        TextView textView = (TextView ) findViewById(R.id.ttttxt);
//        textView.setText(notifJson);


        RecyclerView recyclerView = (RecyclerView) findViewById(
                R.id.notification_recycler_view);
        ContentAdapter adapter = new ContentAdapter(customNotifList);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avator;
        public TextView name;
        public TextView description;

        String offer;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list_notification, parent, false));
            avator = (ImageView) itemView.findViewById(R.id.list_avatar);
            name = (TextView) itemView.findViewById(R.id.list_title);
            description = (TextView) itemView.findViewById(R.id.list_desc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), DetailActivity.class);
//                    FirebaseDatabase.getInstance().getReference("Offers").child(offer.getCity()).child(offer.getCategoryName()).child(offer.getObjectId()).child("viewNum").setValue(offer.getViewNum() + 1);
//                    offer.setViewNum(offer.getViewNum() + 1);
                    intent.putExtra("offer", offer);
                    intent.putExtra("details", Constants.DETAILS_OFFLINE_SHOPS);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {

        ArrayList<CustomNotification> notifications;

        public ContentAdapter(ArrayList<CustomNotification> notifs) {

            this.notifications = notifs;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CustomNotification customNotification = notifications.get(position);
            holder.name.setText(customNotification.getTitle() + " - " + customNotification.getSendingDate());
            holder.description.setText(customNotification.getMessage());
            holder.avator.setImageResource(R.drawable.logo);
            holder.offer = customNotification.getData().get("offer");

        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }
    }

}

