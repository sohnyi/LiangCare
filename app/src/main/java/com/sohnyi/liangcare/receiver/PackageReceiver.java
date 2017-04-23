package com.sohnyi.liangcare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sohnyi.liangcare.database.LiangApp;
import com.sohnyi.liangcare.database.LiangAppLab;

import org.litepal.crud.DataSupport;

/**
 * Created by sohnyi on 2017/4/19.
 */

public class PackageReceiver extends BroadcastReceiver {
    private static final String TAG = "PackageReceiver";
    private static final String ACTION_PACKAGE_ADDED = "android.intent.action.PACKAGE_ADDED";
    private static final String ACTION_PACKAGE_REMOVED = "android.intent.action.PACKAGE_REMOVED";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getDataString();
        if (intent.getAction().equals(ACTION_PACKAGE_ADDED)) {
            LiangAppLab appLab = LiangAppLab.get(context.getApplicationContext());
            appLab.addApp(packageName);
            Log.d(TAG, "onReceive: package added" + packageName);
        } else if (intent.getAction().equals(ACTION_PACKAGE_REMOVED)) {
            DataSupport.deleteAll(LiangApp.class, "packageName = ?", packageName);
            Log.d(TAG, "onReceive: package removed" + packageName);
        }
    }
}
