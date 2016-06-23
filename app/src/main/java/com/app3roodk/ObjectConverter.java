package com.app3roodk;

import com.app3roodk.Schema.Item;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class ObjectConverter {
    final static public String TAG = "ObjectConverter";

    public static String fromArraylistToStringItems(List<Item> items) {
        return new Gson().toJson(items);
    }

    public static List<Item> fromStringToArraylistItems(String arrayListItems) {

        Type type = new TypeToken<List<Item>>() {
        }.getType();
        List<Item> inpList = new Gson().fromJson(arrayListItems, type);
        return inpList;
    }

    public static String fromHashmapToStringUsersRates(HashMap<String, String> usersRates) {
        String stringUsersRates = new Gson().toJson(usersRates);
        return stringUsersRates;
    }

    public static HashMap<String, String> fromStringToHashmapUsersRates(String stringUsersRates) {


        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        HashMap<String, String> hashmapUsersRates = new Gson().fromJson(stringUsersRates, type);
        return hashmapUsersRates;
    }
}
