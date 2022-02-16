package com.example.heartsvalentine;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.material.tabs.TabLayout;

public class Utilities {

    private static TabLayout tabLayout = null;

    public static void setTabLayout(TabLayout tabLayout_) {
        tabLayout = tabLayout_;
    }

    public static int getTotalTopHeight(Context context) {
        Point realScreenSize = getRealScreenSize(context);
        int topHeight = 0;

        Resources res = context.getResources();

        if (res != null) {
            DisplayMetrics metrics = res.getDisplayMetrics();

            if (metrics != null) {
                int mhp = metrics.heightPixels;
                int mwp = metrics.widthPixels;

                if (mhp == realScreenSize.y) {
                    topHeight += (realScreenSize.x - mwp);
                } else {
                    topHeight += (realScreenSize.y - mhp);
                }
            }
        }
        if (tabLayout != null) {
            topHeight += tabLayout.getHeight();
        }

        // Arbitrary correction - seems to give best results across 4 emulators;
        topHeight -= convertDpToPixel(32, context) - 32;

        return topHeight;
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        return size;
    }
    // 2 methods below from  https://androiddvlpr.com/android-dp-to-px/
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
