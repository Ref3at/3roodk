package com.app3roodk.UI.CitySelect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.app3roodk.R;
import com.app3roodk.UI.CitySelect.AnimatedExpandableListView.AnimatedExpandableListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class CitySelectionActivity extends AppCompatActivity {

    List<GroupItem> items;
    DatabaseReference stateNode;
    ValueEventListener eventListener;
    private AnimatedExpandableListView listView;
    private ExampleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);

        stateNode = FirebaseDatabase.getInstance().getReference("COUNTRIES")
                .child("EGYPT").child("GovAndCitiesName");

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    GroupItem item = new GroupItem();
                    item.title = postSnapshot.getKey();
                    Type type = new TypeToken<TreeSet<String>>() {
                    }.getType();
                    TreeSet<String> treeSet = new Gson().fromJson(postSnapshot.getValue().toString(), type);
                    List<String> list = new ArrayList<String>(treeSet);
                    for (int j = 0; j < list.size(); j++) {
                        ChildItem child = new ChildItem();
                        child.title = list.get(j);
                        item.items.add(child);
                    }

                    items.add(item);
                }

                adapter.setData(items);
                listView.setAdapter(adapter);

                try {
                    stateNode.removeEventListener(eventListener);

                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CitySelectionActivity.this, "خطأ ف الاتصال", Toast.LENGTH_SHORT).show();
            }
        };

        stateNode.addValueEventListener(eventListener);

        items = new ArrayList<GroupItem>();

        adapter = new ExampleAdapter(this);
//        adapter.setData(items);
//
        listView = (AnimatedExpandableListView) findViewById(R.id.listView);
//        listView.setAdapter(adapter);

        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        listView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                GroupItem groupItem = items.get(groupPosition);
                String gov = groupItem.title;
                ChildItem childItem = groupItem.items.get(childPosition);
                String city = childItem.title;

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", city);
                returnIntent.putExtra("gov", gov);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

                return false;
            }
        });
    }


    private void fetchList(List<String> list) {

        for (int i = 0; i < 1; i++) {
            GroupItem item = new GroupItem();
            item.title = "المحافظه 5";

            for (int j = 0; j < list.size(); j++) {
                ChildItem child = new ChildItem();
                child.title = list.get(j);
//                child.hint = "Too awesome";

                item.items.add(child);
            }

            items.add(item);
        }

        adapter.setData(items);
        listView.setAdapter(adapter);

    }


    private static class GroupItem {
        String title;
        List<ChildItem> items = new ArrayList<ChildItem>();
    }

    private static class ChildItem {
        String title;
//        String hint;
    }

    private static class ChildHolder {
        TextView title;
        TextView hint;
    }

    private static class GroupHolder {
        TextView title;
    }

    /**
     * Adapter for our list of {@link GroupItem}s.
     */
    private class ExampleAdapter extends AnimatedExpandableListAdapter {
        private LayoutInflater inflater;

        private List<GroupItem> items;

        public ExampleAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void setData(List<GroupItem> items) {
            this.items = items;
        }

        @Override
        public ChildItem getChild(int groupPosition, int childPosition) {
            return items.get(groupPosition).items.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder holder;
            ChildItem item = getChild(groupPosition, childPosition);
            if (convertView == null) {
                holder = new ChildHolder();
                convertView = inflater.inflate(R.layout.list_item, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.textTitle);
//                holder.hint = (TextView) convertView.findViewById(R.id.textHint);
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }

            holder.title.setText(item.title);
//            holder.hint.setText(item.hint);


            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return items.get(groupPosition).items.size();
        }

        @Override
        public GroupItem getGroup(int groupPosition) {
            return items.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return items.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder holder;
            GroupItem item = getGroup(groupPosition);
            if (convertView == null) {
                holder = new GroupHolder();
                convertView = inflater.inflate(R.layout.group_item, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.textTitle);
                convertView.setTag(holder);
            } else {
                holder = (GroupHolder) convertView.getTag();
            }

            holder.title.setText(item.title);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int arg0, int arg1) {
            return true;
        }

    }

}