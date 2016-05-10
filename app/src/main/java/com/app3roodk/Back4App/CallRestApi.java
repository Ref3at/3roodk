package com.app3roodk.Back4App;

import android.content.Context;
import android.util.Log;

import com.app3roodk.R;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.nio.charset.Charset;
import java.util.HashMap;

import cz.msebera.android.httpclient.entity.StringEntity;

public class CallRestApi {
    final static private String TAG = "CallRestApi";

    static public void GET(Context context, String ClassName, HashMap Conditions, TextHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams;
        client.addHeader("X-Parse-Application-Id", context.getString(R.string.PARSE_APPLICATION_ID));
        client.addHeader("X-Parse-REST-API-Key", context.getString(R.string.PARSE_REST_API_KEY));
        if (Conditions != null && Conditions.size() > 0)
            requestParams = new RequestParams("where", new Gson().toJson(Conditions));
        else requestParams = null;
        client.get(context.getString(R.string.URI) + ClassName, requestParams, handler);
    }

    static public void GET(Context context, String ClassName, HashMap Conditions, String orderColumnName, TextHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        client.addHeader("X-Parse-Application-Id", context.getString(R.string.PARSE_APPLICATION_ID));
        client.addHeader("X-Parse-REST-API-Key", context.getString(R.string.PARSE_REST_API_KEY));
        if (Conditions != null && Conditions.size() > 0)
            requestParams.add("where", new Gson().toJson(Conditions));
        requestParams.add("order", orderColumnName);
        client.get(context.getString(R.string.URI) + ClassName, requestParams, handler);
    }

    static public void GET(Context context, String ClassName, String objectId, TextHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Parse-Application-Id", context.getString(R.string.PARSE_APPLICATION_ID));
        client.addHeader("X-Parse-REST-API-Key", context.getString(R.string.PARSE_REST_API_KEY));
        client.get(context.getString(R.string.URI) + ClassName + "/" + objectId, null, handler);
    }

    static public void POST(Context context, String ClassName, HashMap Properties, TextHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Parse-Application-Id", context.getString(R.string.PARSE_APPLICATION_ID));
        client.addHeader("X-Parse-REST-API-Key", context.getString(R.string.PARSE_REST_API_KEY));
        StringEntity entity;
        if (Properties == null) {
            Log.e(TAG, "Properties is null");
            return;
        } else {
            try {
                entity = new StringEntity(new Gson().toJson(Properties), Charset.defaultCharset());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return;
            }
        }
        client.post(context, context.getString(R.string.URI) + ClassName, entity, "application/json", handler);
    }

    static public void PUT(Context context, String ClassName, HashMap UpdateValues, String objectId, TextHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Parse-Application-Id", context.getString(R.string.PARSE_APPLICATION_ID));
        client.addHeader("X-Parse-REST-API-Key", context.getString(R.string.PARSE_REST_API_KEY));
        if (objectId.isEmpty()) {
            Log.e(TAG, "No objectId exits");
            return;
        }
        if (UpdateValues == null) {
            Log.e(TAG, "UpdateValues is null");
            return;
        }
        StringEntity entity;
        try {
            entity = new StringEntity(new Gson().toJson(UpdateValues), Charset.defaultCharset());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return;
        }
        client.put(context, context.getString(R.string.URI) + ClassName + "/" + objectId, entity, "application/json", handler);
    }

    static public void INCREMENT(Context context, String className, String objectId, String columnName, int amount, TextHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Parse-Application-Id", context.getString(R.string.PARSE_APPLICATION_ID));
        client.addHeader("X-Parse-REST-API-Key", context.getString(R.string.PARSE_REST_API_KEY));
        if (objectId.isEmpty()) {
            Log.e(TAG, "No objectId exits");
            return;
        }
        if (columnName.isEmpty()) {
            Log.e(TAG, "Column name is empty");
            return;
        }
        HashMap map = new HashMap();
        map.put("__op", "Increment");
        map.put("amount", amount);
        HashMap mapFinal = new HashMap();
        mapFinal.put(columnName, map);
        StringEntity entity;
        try {
            entity = new StringEntity(new Gson().toJson(mapFinal), Charset.defaultCharset());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return;
        }
        client.put(context, context.getString(R.string.URI) + className + "/" + objectId, entity, "application/json", handler);
    }

    static public void INCREMENT_TWO(Context context, String className, String objectId, String columnName1, int amount1, String columnName2, int amount2, TextHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Parse-Application-Id", context.getString(R.string.PARSE_APPLICATION_ID));
        client.addHeader("X-Parse-REST-API-Key", context.getString(R.string.PARSE_REST_API_KEY));
        if (objectId.isEmpty()) {
            Log.e(TAG, "No objectId exits");
            return;
        }
        if (columnName1.isEmpty()) {
            Log.e(TAG, "Column name1 is empty");
            return;
        }
        if (columnName2.isEmpty()) {
            Log.e(TAG, "Column name2 is empty");
            return;
        }
        HashMap map1 = new HashMap();
        map1.put("__op", "Increment");
        map1.put("amount", amount1);
        HashMap map2 = new HashMap();
        map2.put("__op", "Increment");
        map2.put("amount", amount2);
        HashMap mapFinal = new HashMap();
        mapFinal.put(columnName1, map1);
        mapFinal.put(columnName2, map2);
        StringEntity entity;
        try {
            entity = new StringEntity(new Gson().toJson(mapFinal), Charset.defaultCharset());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return;
        }
        client.put(context, context.getString(R.string.URI) + className + "/" + objectId, entity, "application/json", handler);
    }

    static public void INCREMENT_AND_UPDATE(Context context, String className, String objectId, String columnName1, int amount1, int averageRate, TextHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Parse-Application-Id", context.getString(R.string.PARSE_APPLICATION_ID));
        client.addHeader("X-Parse-REST-API-Key", context.getString(R.string.PARSE_REST_API_KEY));
        if (objectId.isEmpty()) {
            Log.e(TAG, "No objectId exits");
            return;
        }
        if (columnName1.isEmpty()) {
            Log.e(TAG, "Column name1 is empty");
            return;
        }
        HashMap map1 = new HashMap();
        map1.put("__op", "Increment");
        map1.put("amount", amount1);
        HashMap mapFinal = new HashMap();
        mapFinal.put(columnName1, map1);
        mapFinal.put("averageRate", averageRate);
        StringEntity entity;
        try {
            entity = new StringEntity(new Gson().toJson(mapFinal), Charset.defaultCharset());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return;
        }
        client.put(context, context.getString(R.string.URI) + className + "/" + objectId, entity, "application/json", handler);
    }

    static public void INCREMENT_TWO_AND_UPDATE(Context context, String className, String objectId, String columnName1, int amount1, String columnName2, int amount2, int averageRate, TextHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Parse-Application-Id", context.getString(R.string.PARSE_APPLICATION_ID));
        client.addHeader("X-Parse-REST-API-Key", context.getString(R.string.PARSE_REST_API_KEY));
        if (objectId.isEmpty()) {
            Log.e(TAG, "No objectId exits");
            return;
        }
        if (columnName1.isEmpty()) {
            Log.e(TAG, "Column name1 is empty");
            return;
        }
        if (columnName2.isEmpty()) {
            Log.e(TAG, "Column name2 is empty");
            return;
        }
        HashMap map1 = new HashMap();
        map1.put("__op", "Increment");
        map1.put("amount", amount1);
        HashMap map2 = new HashMap();
        map2.put("__op", "Increment");
        map2.put("amount", amount2);
        HashMap mapFinal = new HashMap();
        mapFinal.put(columnName1, map1);
        mapFinal.put(columnName2, map2);
        mapFinal.put("averageRate", averageRate);
        StringEntity entity;
        try {
            entity = new StringEntity(new Gson().toJson(mapFinal), Charset.defaultCharset());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return;
        }
        client.put(context, context.getString(R.string.URI) + className + "/" + objectId, entity, "application/json", handler);
    }

    static public void DELETE(Context context, String ClassName, String objectId, TextHttpResponseHandler handler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("X-Parse-Application-Id", context.getString(R.string.PARSE_APPLICATION_ID));
        client.addHeader("X-Parse-REST-API-Key", context.getString(R.string.PARSE_REST_API_KEY));
        if (objectId.isEmpty()) {
            Log.e(TAG, "No objectId exits");
            return;
        }
        client.delete(context.getString(R.string.URI) + ClassName + "/" + objectId, handler);
    }
}
