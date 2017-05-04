package com.sohnyi.liangcare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sohnyi.liangcare.AppLockFragment;

/**
 * Created by sohnyi on 2017/4/19.
 */

public class PackageReceiver extends BroadcastReceiver {
    private static final String TAG = "PackageReceiver";
    public static final String ACTION_PACKAGE_ADDED = "android.intent.action.PACKAGE_ADDED";
    public static final String ACTION_PACKAGE_REMOVED = "android.intent.action.PACKAGE_REMOVED";
    public static final String ACTION_PACKAGE_FULLY_REMOVED = "android.intent.action.PACKAGE_FULLY_REMOVED";

    private SharedPreferences mPreferences;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mPreferences.edit();

        String packageName = intent.getDataString().substring(8);
        if (intent.getAction().equals(ACTION_PACKAGE_ADDED)) {
            Log.d(TAG, "onReceive: package added " + packageName);
        } else if (intent.getAction().equals(ACTION_PACKAGE_REMOVED)
                || intent.getAction().equals(ACTION_PACKAGE_FULLY_REMOVED)) {
            Log.d(TAG, "onReceive: package removed " + packageName);
        }
        editor.putBoolean(AppLockFragment.HAVE_CHANGED, true);
        editor.apply();
    }
}
