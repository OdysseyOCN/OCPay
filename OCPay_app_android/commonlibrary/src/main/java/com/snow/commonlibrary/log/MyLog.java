package com.snow.commonlibrary.log;

import android.util.Log;

/**
 * Created by y on 2017/11/1.
 */

public class MyLog {

    public static final String TAG = "mu_zi";
    public static boolean DEBUG = false;


    public static void i(String msg) {
        if (!DEBUG) return;
        Log.i(TAG, msg);
    }

    public static void w(String msg) {
        if (!DEBUG) return;
        Log.w(TAG, msg);
    }


    public static void e(String msg) {
        if (!DEBUG) return;
        Log.e(TAG, msg);
    }

}
