package com.sohnyi.liangcare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sohnyi.liangcare.service.LockService;


/**
 * Created by sohnyi on 2017/4/19.
 */

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i= LockService.newIntent(context);
        context.startService(i);
    }
}
