package com.app3roodk.UI.MainActivity;

/**
 * Created by ultra book on 10/12/2016.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app3roodk.R;


public class MapFragment extends Fragment {

    public static String SelectedCity;
    public static String SelectedGov;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        //showMessage( "هنا خريطه " + SelectedGov +" - "+ SelectedCity);
        Toast.makeText(getActivity(), "اشتغلت", Toast.LENGTH_SHORT).show();
        return rootView;
    }

    private void showMessage(String msg) {
        if (isAdded() && !isDetached() && isVisible())
            Snackbar.make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
    }


}