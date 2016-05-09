package com.app3roodk.UI.FullScreenImage;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Refaat on 5/9/2016.
 */
public class RefaatPager extends ViewPager {
    public RefaatPager(Context context) {
        super(context);
    }

    public RefaatPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}
