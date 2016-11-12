package com.app3roodk.Holders;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.app3roodk.R;

public class HolderShopsCodeBuilder {

    private Button btnDone;
    private EditText edtxtCode;
    private AlertDialog mAlertDialog;
    private Activity mActivity;

    public HolderShopsCodeBuilder(View rootView, Activity activity, AlertDialog alertDialog) {
        mAlertDialog = alertDialog;
        mActivity = activity;
        init(rootView);
    }

    private void init(View rootView) {
        btnDone = (Button) rootView.findViewById(R.id.btn_dialog_done);
        edtxtCode = (EditText) rootView.findViewById(R.id.etxt_code);

        rootView.findViewById(R.id.btn_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "01001349907")));
            }
        });

        rootView.findViewById(R.id.btn_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
    }

    public boolean isCodeEmpty() {
        return edtxtCode.getText().toString().isEmpty();
    }

    public String getCode() {
        return edtxtCode.getText().toString();
    }

    public void setDoneClickListener(View.OnClickListener listener) {
        btnDone.setOnClickListener(listener);
    }

}
