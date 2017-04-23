package com.sohnyi.liangcare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sohnyi.liangcare.service.PackageService;
import com.sohnyi.liangcare.utils.ShowToast;


/**
 * Created by sohnyi on 2017/4/19.
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        ShowToast.showToast(context, TAG + "boot complete");
        Intent i= PackageService.newIntent(context);
        context.startService(i);
        Log.d(TAG, "onReceive: ");
    }
}
