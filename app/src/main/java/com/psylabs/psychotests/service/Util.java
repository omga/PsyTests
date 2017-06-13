package com.psylabs.psychotests.service;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Andrew on 11.04.2017.
 */

public class Util {
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
