package com.app3roodk.UI.IntroActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.app3roodk.R;

/**
 * Created by ultra book on 16-Oct-16.
 */

public class IntroScreen5 extends Fragment {

    TextView txtTitle;
    CheckBox notf_subsc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.intro_screen_5, container, false);

        txtTitle = (TextView) rootView.findViewById(R.id.txt_titlec);
        notf_subsc = (CheckBox) rootView.findViewById(R.id.subsc_notif);

        return rootView;
    }

    void updatingCity(String c) {

        txtTitle.setText("أول بأول أحدث عروض مدينه " + c);
        notf_subsc.setText("اشترك ليصلك تنبيهات احد عروض " + c);
    }

}
