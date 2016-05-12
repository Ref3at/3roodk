package com.app3roodk.UI.Signing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app3roodk.Back4App.UtilityRestApi;
import com.app3roodk.ObjectConverter;
import com.app3roodk.R;
import com.app3roodk.UtilityGeneral;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ZooM- on 5/7/2016.
 */
public class SignInFragment extends Fragment {

    Button btnSignIn;
    EditText edtxtUserEmail, edtxtUserPassword;
    TextView txtError;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signin, container, false);
        init(rootView);
        clickConfig();
        return rootView;
    }

    private void init(View rootView) {
        edtxtUserEmail = (EditText) rootView.findViewById(R.id.edtxtEmail);
        edtxtUserPassword = (EditText) rootView.findViewById(R.id.edtxtPassword);
        txtError = (TextView) rootView.findViewById(R.id.txtError);
        btnSignIn = (Button) rootView.findViewById(R.id.btnSignIn);
    }

    private void clickConfig() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInputs())
                    return;
                UtilityRestApi.loginUser(getActivity().getBaseContext(), edtxtUserEmail.getText().toString(), edtxtUserPassword.getText().toString(), new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        showErrorMessage("Check your internet connection");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            JSONArray result = new JSONObject(responseString).getJSONArray("results");
                            if (result.isNull(0)) {
                                showErrorMessage("البريد الإلكتروني او كلمة السر غير صحيحة");
                                return;
                            }
                            UtilityGeneral.saveUser(getActivity(), ObjectConverter.convertJsonToUser(result.getJSONObject(0)));
                            getActivity().onBackPressed();
                        } catch (Exception ex) {
                            Log.e("SignInFragment", ex.getMessage());
                        }
                    }
                });
            }
        });
    }

    private boolean checkInputs() {
        showErrorMessage("");
        if (edtxtUserEmail.getText().toString().isEmpty()) {
            showErrorMessage("أدخل البريد الإلكتروني");
            return false;
        }
        if (edtxtUserPassword.getText().toString().isEmpty()) {
            showErrorMessage("أدخل كلمة السر");
            return false;
        }
        return true;
    }

    private void showErrorMessage(String message) {
        txtError.setText(message);
    }
}
