package com.sohnyi.liangcare.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by Ziyi on 2016/9/4.
 */
public class PermissionsChecker {
    private final Context mContext;

    public PermissionsChecker(Context context) {
        mContext = context.getApplicationContext();
    }

    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions)
            if (lacksPermission(permission)) {
                return true;
            }
        return false;
    }

    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission)
                == PackageManager.PERMISSION_DENIED;
    }

    public boolean canDrawOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (Settings.canDrawOverlays(mContext)) {
                    return true;
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "canDrawOverlays: " + e.getMessage());
            }
        }
        return false;
    }
}

