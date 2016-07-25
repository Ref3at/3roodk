package com.app3roodk.Imgur;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by Refaat on 5/9/2016.
 */
public class ImgurSampleApplication extends Application {

    private static final String TAG = ImgurSampleApplication.class.getSimpleName();

    private static Context context;

    /**
     * http://stackoverflow.com/questions/2002288/static-way-to-get-context-on-android
     */
    public static Context getAppContext() {
        return ImgurSampleApplication.context;
    }

    public static String getVersionName() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
//            Log.e(TAG, "Package name not found.", e);
            return "1.0";
        }
    }

    public void onCreate() {
        super.onCreate();
        ImgurSampleApplication.context = getApplicationContext();
    }

}
