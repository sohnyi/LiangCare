package com.sohnyi.liangcare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sohnyi.liangcare.service.LockService;
import com.sohnyi.liangcare.utils.LogUtil;
import com.sohnyi.liangcare.utils.ToastUtil;


/**
 * Created by sohnyi on 2017/4/19.
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        ToastUtil.showToast(context, TAG + "boot complete");
        Intent i= LockService.newIntent(context);
        context.startService(i);


        LogUtil.d(TAG, "onReceive: ");
    }
}
