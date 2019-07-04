
package com.mxn.soul.flowingpager_core;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by mxn on 2019/7/1.
 * NoClickThroughFrameLayout
 */
public class NoClickThroughFrameLayout extends BuildLayerFrameLayout {

    public NoClickThroughFrameLayout(Context context) {
        super(context);
    }

    public NoClickThroughFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoClickThroughFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
