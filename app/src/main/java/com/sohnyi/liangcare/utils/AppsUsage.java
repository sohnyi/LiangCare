package com.sohnyi.liangcare.utils;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.Collections;
import java.util.List;

/**
 * Created by sohnyi on 2017/4/19.
 */

public class AppsUsage {
    /*获取过去1分钟内，APP启动情况的统计数据*/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static List getUsageStatsList(Context context) {
        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(
                Context.USAGE_STATS_SERVICE);
        if ( manager != null) {
            long timeNow = System.currentTimeMillis();
            return manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                    timeNow - 60 * 1000, timeNow);
        }
        return Collections.EMPTY_LIST;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean isStatAccessPermissionSet(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager opsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            return opsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid,
                    info.packageName) == AppOpsManager.MODE_ALLOWED;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
