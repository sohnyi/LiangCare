package com.sohnyi.liangcare.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sohnyi.liangcare.R;
import com.sohnyi.liangcare.receiver.BootCompleteReceiver;

/**
 * Created by sohnyi on 2017/4/19.
 */

public class PackageService extends Service {
    private static final String TAG = "PackageService";
    private static final int SERVICE_NOTIFICATION_ID = 1;

    public PackageService() {
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, PackageService.class);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, BootCompleteReceiver.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getText(R.string.app_lock))
                .setContentText(getText(R.string.click_stop))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .build();
        startForeground(SERVICE_NOTIFICATION_ID, notification);
        Log.d(TAG, "onStartCommand: notification start foreground");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        Log.d(TAG, "onDestroy: onDestroy executed");
    }

}
