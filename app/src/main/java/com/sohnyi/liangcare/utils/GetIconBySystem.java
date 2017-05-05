package com.sohnyi.liangcare.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by sohnyi on 2017/4/25.
 */

public class GetIconBySystem {
    public static Drawable getIcon(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            Drawable icon =pm.getApplicationIcon(packageName);
            return icon;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.d(TAG, "getIconBySystem: " + e.getMessage());
        }
        return null;
    }
}
