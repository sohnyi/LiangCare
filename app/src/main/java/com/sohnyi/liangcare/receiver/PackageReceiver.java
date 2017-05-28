package com.sohnyi.liangcare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sohnyi.liangcare.database.LiangAppLab;

import static com.sohnyi.liangcare.database.MyConstant.MYPACKAGENAME;

/**
 * Created by sohnyi on 2017/4/19.
 */

public class PackageReceiver extends BroadcastReceiver {
    private static final String TAG = "PackageReceiver";
    public static final String ACTION_PACKAGE_ADDED = "android.intent.action.PACKAGE_ADDED";
    public static final String ACTION_PACKAGE_REMOVED = "android.intent.action.PACKAGE_REMOVED";
    public static final String ACTION_PACKAGE_FULLY_REMOVED = "android.intent.action.PACKAGE_FULLY_REMOVED";

    private LiangAppLab mAppLab;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        mAppLab = LiangAppLab.get();

        String packageName = intent.getDataString().substring(8);
        if (intent.getAction().equals(ACTION_PACKAGE_ADDED)) {
            if (!packageName.equals(MYPACKAGENAME)) {
                mAppLab.addApp(packageName);
            }
        } else if (intent.getAction().equals(ACTION_PACKAGE_REMOVED)
                || intent.getAction().equals(ACTION_PACKAGE_FULLY_REMOVED)) {
            if (!packageName.equals(MYPACKAGENAME)) {
                mAppLab.deleteApp(packageName);
            }
        }
    }
}
