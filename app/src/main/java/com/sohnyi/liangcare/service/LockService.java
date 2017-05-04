package com.sohnyi.liangcare.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.sohnyi.liangcare.AppLockActivity;
import com.sohnyi.liangcare.R;
import com.sohnyi.liangcare.database.LiangApp;
import com.sohnyi.liangcare.utils.AppsUsage;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by sohnyi on 2017/4/19.
 */

public class LockService extends Service {
    private static final String TAG = "LockService";
    private static final int SERVICE_NOTIFICATION_ID = 1;
    public static final String UNLOCK_ACTION = "com.sohnyi.liang.unlock";

    private ActivityManager mActivityManager;
    private SharedPreferences mPreferences;

    private boolean isLock;

    public LockService() {
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, LockService.class);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification(getString(R.string.app_lock), getString(R.string.click_stop));

        new Thread(new Runnable() {
            @Override
            public void run() {
                checkLockState();
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        Log.d(TAG, "onDestroy: onDestroy executed");
    }

    private void showNotification(String title, String message) {
        Intent notificationIntent = new Intent(this, AppLockActivity.class);


        PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pi)
                .build();
        startForeground(SERVICE_NOTIFICATION_ID, notification);
        Log.d(TAG, "onStartCommand: notification start foreground");
    }


    private void checkLockState() {
        while (true) {
            String topPackageName = AppsUsage.getLauncherTopApp(this, mActivityManager);

            if (!TextUtils.isEmpty(topPackageName)) {
                try {
                    List<LiangApp> apps = apps = DataSupport.where("packageName = ?", topPackageName)
                            .find(LiangApp.class);
                    if (apps.size() == 1) {
                        LiangApp app = apps.get(0);
                        boolean needLock = app.isLock();
                        if (needLock) {
                            Log.d(TAG, "checkLockState: need lock");
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "checkLockState: " + e.getMessage());
                }
            }


        }
    }

}
