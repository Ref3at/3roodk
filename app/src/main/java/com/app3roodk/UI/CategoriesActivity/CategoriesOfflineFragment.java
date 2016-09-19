package com.app3roodk.UI.CategoriesActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.app3roodk.R;
import com.app3roodk.Schema.User;
import com.app3roodk.UtilityFirebase;
import com.app3roodk.UtilityGeneral;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.TextHttpResponseHandler;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class CategoriesOfflineFragment extends Fragment {

    Spinner spnCities;
    ArrayList<String> lstCities;
    ArrayAdapter<String> adapter;
    SpinKitView progress;

    private static final String[] CHANNELS = new String[]{ "هايبرات","محلات"};
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private FragmentContainerHelper mFragmentContainerHelper = new FragmentContainerHelper();
    private CategoriesOfflineShopsFragment mShopsFragment;
    private CategoriesOfflineHypersFragment mHypersFragment;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories_offline, container, false);
        init(rootView);
        initFragments();
        initMagicIndicator1(rootView);

        mFragmentContainerHelper.handlePageSelected(1, false);
        switchPages(1);
        return rootView;
    }

    private void switchPages(int index) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;
        for (int i = 0, j = mFragments.size(); i < j; i++) {
            if (i == index) {
                continue;
            }
            fragment = mFragments.get(i);
            if (fragment.isAdded()) {
                fragmentTransaction.hide(fragment);
            }
        }
        fragment = mFragments.get(index);
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.add(R.id.fragment_container, fragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void initFragments() {
        mHypersFragment = new CategoriesOfflineHypersFragment();
        mFragments.add(mHypersFragment);
        mShopsFragment = new CategoriesOfflineShopsFragment();
        mFragments.add(mShopsFragment);
    }

    private void initMagicIndicator1(View rootView) {
        MagicIndicator magicIndicator = (MagicIndicator) rootView.findViewById(R.id.magic_indicator);
        magicIndicator.setBackgroundResource(R.drawable.round_indicator_bg);
        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return CHANNELS.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
                clipPagerTitleView.setText(CHANNELS[index]);
                clipPagerTitleView.setTextColor(Color.parseColor("#e94220"));
                clipPagerTitleView.setClipColor(Color.WHITE);
                clipPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFragmentContainerHelper.handlePageSelected(index);
                        switchPages(index);
                    }
                });
                return clipPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                float navigatorHeight = context.getResources().getDimension(R.dimen.common_navigator_height);
                float borderWidth = UIUtil.dip2px(context, 1);
                float lineHeight = navigatorHeight - 2 * borderWidth;
                indicator.setLineHeight(lineHeight);
                indicator.setRoundRadius(lineHeight / 2);
                indicator.setYOffset(borderWidth);
                indicator.setColors(Color.parseColor("#bc2a2a"));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        mFragmentContainerHelper.attachMagicIndicator(magicIndicator);
    }

    private void init(View rootView) {

        try {
            if (!UtilityGeneral.mGoogleApiClient.isConnected() && !UtilityGeneral.mGoogleApiClient.isConnecting())
                UtilityGeneral.mGoogleApiClient.connect();
        } catch (Exception ex) {
        }
        progress = (SpinKitView) rootView.findViewById(R.id.progress);
        spnCities = (Spinner) rootView.findViewById(R.id.spnCities);
        clickConfig(rootView);
        updateUserLocation();
        initSpinner();
        loadCity();
        rootView.findViewById(R.id.spnCities).setVisibility(View.VISIBLE);
    }

    private void updateUserLocation() {
        try {
            User user = UtilityGeneral.loadUser(getActivity());
            LatLng location = UtilityGeneral.getCurrentLonAndLat(getActivity());
            if (location == null) return;
            user.setLat(String.valueOf(location.latitude));
            user.setLon(String.valueOf(location.longitude));
            UtilityGeneral.saveUser(getActivity(), user);
        } catch (Exception ex) {
        }
    }

    private void showMessage(String msg) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
    }


    private void clickConfig(View rootView) {

    }

    private void initSpinner() {
        lstCities = UtilityGeneral.loadCities(getActivity());
        adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, lstCities);
        spnCities.setAdapter(adapter);
        spnCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                UtilityGeneral.saveSpinnerCity(getActivity(),lstCities.get(i));
                mShopsFragment.changeCities(i,lstCities);
                mHypersFragment.changeCities(i,lstCities);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        String choosedCity = UtilityGeneral.loadSpinnerCity(getActivity());
        if(!choosedCity.equals("No"))
        {
            for(int i = 0 ; i<lstCities.size();i++)
                if(lstCities.get(i).equals(choosedCity))
                {
                    spnCities.setSelection(i);
                    break;
                }
        }
    }

    private void loadCity() {
        UtilityFirebase.getCities(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    UtilityGeneral.saveCities(getActivity(), responseString);
                    initSpinner();
                } catch (Exception ex) {
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                UtilityGeneral.getCurrentCityArabic(getActivity());
            }
        }).start();
    }

}
