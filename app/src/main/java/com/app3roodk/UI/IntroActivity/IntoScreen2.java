package com.app3roodk.UI.IntroActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app3roodk.R;

/**
 * Created by ultra book on 16-Oct-16.
 */

public class IntoScreen2 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.intro_screen_2, container, false);

        return rootView;
    }
}
