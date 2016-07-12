package com.app3roodk.UI.Feedback;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.app3roodk.R;
import com.app3roodk.Schema.Feedback;
import com.app3roodk.UtilityFirebase;
import com.app3roodk.UtilityGeneral;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class FeedbackFragment extends Fragment {

    EditText edtxtName, edtxtEmail, edtxtTitle, edtxtContent;
    TextInputLayout txtlytTitle, txtlytContent;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feedback, container, false);
        init(rootView);
        validateUser();
        clickConfig(rootView);
        return rootView;
    }


    private void init(View rootView) {
        edtxtTitle = (EditText) rootView.findViewById(R.id.edtxtTitle);
        edtxtContent = (EditText) rootView.findViewById(R.id.edtxtContent);
        edtxtEmail = (EditText) rootView.findViewById(R.id.edtxtEmail);
        edtxtName = (EditText) rootView.findViewById(R.id.edtxtName);
        txtlytTitle = (TextInputLayout) rootView.findViewById(R.id.txtlytTitle);
        txtlytContent = (TextInputLayout) rootView.findViewById(R.id.txtlytContent);
    }

    private void validateUser() {
        if (UtilityGeneral.isRegisteredUser(getActivity())) {
            edtxtEmail.setVisibility(View.GONE);
            edtxtName.setVisibility(View.GONE);
        } else {
            edtxtEmail.setVisibility(View.VISIBLE);
            edtxtName.setVisibility(View.VISIBLE);
        }
    }

    private void clickConfig(View rootView) {
        rootView.findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validation()) return;
                Feedback feedback = new Feedback();
                feedback.setRegisteredUser(UtilityGeneral.isRegisteredUser(getActivity()));
                if (feedback.isRegisteredUser()) {
                    feedback.setEmail(UtilityGeneral.loadUser(getActivity()).getObjectId());
                    feedback.setEmail(UtilityGeneral.loadUser(getActivity()).getEmail());
                    feedback.setName(UtilityGeneral.loadUser(getActivity()).getName());

                } else {
                    feedback.setEmail(edtxtEmail.getText().toString());
                    feedback.setName(edtxtName.getText().toString());
                }
                feedback.setContent(edtxtContent.getText().toString());
                feedback.setTitle(edtxtTitle.getText().toString());
                AppendReportInfo(feedback);
                showMessageToast("جاري إرسال الرسالة");
                UtilityFirebase.sendFeedback(feedback, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            showMessageToast("شكراً لك، تم إرسال الرسالة بنجاح");
                            getActivity().finish();
                        } else {
                            showMessageToast("حدث خطأ أثناء الإرسال، بص على النت كده!");
                        }
                    }
                });
            }
        });
    }

    private void showMessageToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    private boolean validation() {
        if (edtxtTitle.getText().toString().trim().isEmpty()) {
            txtlytTitle.setError("إدخل عنوان للرسالة");
            requestFocus(edtxtTitle);
            return false;
        } else {
            txtlytTitle.setErrorEnabled(false);
        }

        if (edtxtContent.getText().toString().trim().isEmpty()) {
            txtlytContent.setError("إدخل محتوى الرسالة");
            requestFocus(edtxtContent);
            return false;
        } else {
            txtlytTitle.setErrorEnabled(false);
        }

        return true;
    }

    private void AppendReportInfo(Feedback feedback) {
        try {
            if (getActivity().getIntent().getExtras().getString("type") != null) {
                feedback.setType(getActivity().getIntent().getExtras().getString("type"));
                feedback.setTypePath(getActivity().getIntent().getExtras().getString("typePath"));
            }
        } catch (Exception ex) {
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
