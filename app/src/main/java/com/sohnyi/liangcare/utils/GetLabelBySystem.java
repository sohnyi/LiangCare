package com.sohnyi.liangcare.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by sohnyi on 2017/4/26.
 */

public class GetLabelBySystem {
    public static String getLabel(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
           return pm.getApplicationLabel(pm.getApplicationInfo(
                    packageName, PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "getLabel: " + e.getMessage());
        }
        return null;
    }
}
