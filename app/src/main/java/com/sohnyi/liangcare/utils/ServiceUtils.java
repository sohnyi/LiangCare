package com.sohnyi.liangcare.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by sohnyi on 2017/5/5.
 */

public class ServiceUtils {
    private static final int MAX_SERVICES_NUM = 100;

    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = manager.getRunningServices(MAX_SERVICES_NUM);

        for (ActivityManager.RunningServiceInfo info : serviceInfos) {
            String name = info.service.getClassName();
            if (serviceName.equals(name)) {
                LogUtil.d(TAG, "isServiceRunning: service name" + name);
                return true;
            }
        }
        return false;

    }

}
