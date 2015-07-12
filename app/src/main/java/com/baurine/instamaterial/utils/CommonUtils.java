package com.baurine.instamaterial.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class CommonUtils {

    private static int mScreenWidth = 0;
    private static int mScreenHeight = 0;

    private static void getScreenMetric(Context cxt) {
        WindowManager wm = (WindowManager) cxt.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;
    }

    public static int getScreenWidth(Context cxt) {
        if (mScreenHeight == 0) {
            getScreenMetric(cxt);
        }

        return mScreenWidth;
    }

    public static int getScreenHeight(Context cxt) {
        if (mScreenHeight == 0) {
            getScreenMetric(cxt);
        }

        return mScreenHeight;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}
