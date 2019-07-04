package com.mxn.soul.flowingpager_core;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by mxn on 2019/7/1.
 * FlowingPagerUtils
 */
class FlowingPagerUtils {

    public static double OPEN_RATIO = 0.25 ;
    // coefficient
    public static final float COEF1 = 1.7f ;
    public static final float COEF2 = 0.6f ;
    public static final float COEF3 = 3f ;
    public static final float COEF4 = 0.4f ;
    public static final float COEF5 = 1.2f ;
    public static final float COEF6 = 1.5f ;
    public static final float COEF7 = 0.25f ;
    public static final float COEF8 = 0.5f ;

    static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        // 屏幕宽度（像素）
        return dm.widthPixels;
    }

    public static void setOpenRatio(double openRatio) {
        if (openRatio > 0 && openRatio < 1) {
            OPEN_RATIO = openRatio;
        }
    }
}
