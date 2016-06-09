package com.app3roodk.UI.Signing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.app3roodk.Back4App.UtilityRestApi;
import com.app3roodk.ObjectConverter;
import com.app3roodk.R;
import com.app3roodk.Schema.User;
import com.app3roodk.UtilityGeneral;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ZooM- on 5/7/2016.
 */
public class SignUpFragment extends Fragment {

    Button btnRegister;
    EditText edtxtUserName, edtxtUserEmail, edtxtUserPassword, edtxtUserConfirmPassword;
    TextView txtError;
    RadioButton rbtnMale, rbtnFemale;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        init(rootView);
        clickConfig();
        return rootView;
    }

    private void init(View rootView) {
        edtxtUserName = (EditText) rootView.findViewById(R.id.edtxtUserName);
        edtxtUserConfirmPassword = (EditText) rootView.findViewById(R.id.edtxtRePassword);
        edtxtUserEmail = (EditText) rootView.findViewById(R.id.edtxtEmail);
        edtxtUserPassword = (EditText) rootView.findViewById(R.id.edtxtPassword);
        txtError = (TextView) rootView.findViewById(R.id.txtError);
        btnRegister = (Button) rootView.findViewById(R.id.btnRegister);
        rbtnMale = (RadioButton) rootView.findViewById(R.id.rbtnMale);
        rbtnFemale = (RadioButton) rootView.findViewById(R.id.rbtnFemale);
        rbtnMale.setChecked(true);
    }

    private void clickConfig() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInputs())
                    return;
                UtilityRestApi.getUserByEmail(getActivity().getBaseContext(), edtxtUserEmail.getText().toString(), new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        showErrorMessage("Check your internet connection");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            JSONArray result = new JSONObject(responseString).getJSONArray("results");
                            if (result.length() > 0) {
                                showErrorMessage("هذا البريد الإلكتروني مسجل سابقاً");
                            } else {
                                user = new User();
                                LatLng latLng = UtilityGeneral.getCurrentLonAndLat(getActivity());
                                user.setName(edtxtUserName.getText().toString());
                                user.setEmail(edtxtUserEmail.getText().toString());
                                user.setLat(String.valueOf(latLng.latitude));
                                user.setLon(String.valueOf(latLng.longitude));
                                if (rbtnMale.isChecked())
                                    user.setGender("Male");
                                else
                                    user.setGender("Female");
                                UtilityRestApi.registerNewUser(getActivity().getBaseContext(), ObjectConverter.convertUserToHashMap(user), new TextHttpResponseHandler() {
                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        showErrorMessage("Check your internet connection");
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                        try {
                                            JSONObject result = new JSONObject(responseString);
                                            user.setObjectId(result.getString("objectId"));
                                            user.setCreatedAt(result.getString("createdAt"));
                                            UtilityGeneral.saveUser(getActivity(), user);
                                            getActivity().onBackPressed();
                                            showErrorMessage("");
                                        } catch (JSONException e) {
                                            showErrorMessage(e.getMessage());
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            showErrorMessage(e.getMessage());
                        }
                    }
                });
            }
        });
    }

    private boolean checkInputs() {
        showErrorMessage("");
        if (edtxtUserName.getText().toString().isEmpty()) {
            showErrorMessage("أدخل اسم المستخدم");
            return false;
        }
        if (edtxtUserPassword.getText().toString().isEmpty()) {
            showErrorMessage("أدخل كلمة السر");
            return false;
        }
        if (edtxtUserEmail.getText().toString().isEmpty()) {
            showErrorMessage("أدخل البريد الإلكتروني");
            return false;
        } else if (!edtxtUserPassword.getText().toString().equals(edtxtUserPassword.getText().toString())) {
            showErrorMessage("كلمة السر غير متطابقة");
            return false;
        }
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            builder.show();
            return false;
        }
        if (UtilityGeneral.getCurrentLonAndLat(getActivity()) == null) {
            showErrorMessage("Make sure that Location Permission is allowed on your device!");
            return false;
        }
        return true;
    }

    private void showErrorMessage(String message) {
        try {
            txtError.setText(message);
        } catch (Exception ex) {
            Log.e("SignUpFragment", ex.getMessage());
        }
    }
}
