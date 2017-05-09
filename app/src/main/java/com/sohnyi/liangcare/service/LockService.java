package com.sohnyi.liangcare.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.sohnyi.liangcare.AppLockActivity;
import com.sohnyi.liangcare.R;
import com.sohnyi.liangcare.database.LiangApp;
import com.sohnyi.liangcare.ui.LoginActivity;
import com.sohnyi.liangcare.utils.AppsUsage;
import com.sohnyi.liangcare.utils.LogUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by sohnyi on 2017/4/19.
 */

public class LockService extends Service {
    private static final String TAG = "LockService";
    private static final int SERVICE_NOTIFICATION_ID = 1;
    public static final String UNLOCK_ACTION = "UNLOCK_ACTION";

    private ScreenReceiver mServiceReceiver;
    private static String lastLockPackage;
    private ActivityManager mActivityManager;
    private SharedPreferences mPreferences;


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
        lastLockPackage = "";

        //注册广播
        mServiceReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(UNLOCK_ACTION);
        registerReceiver(mServiceReceiver, filter);
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
        LogUtil.d(TAG, "onDestroy: onDestroy executed");
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
        LogUtil.d(TAG, "onStartCommand: notification start foreground");
    }

    private void checkLockState() {
        while (true) {
            String topPackageName = AppsUsage.getLauncherTopApp(this, mActivityManager);

            if (!TextUtils.isEmpty(topPackageName)) {
                if (topPackageName.equals("com.tencent.mobileqq")) {
                    LogUtil.d(TAG, "need lock com.tencent.mobileqq");
                }
                if ((!topPackageName.equals(lastLockPackage)) && (!topPackageName.equals(getPackageName()))
                        && (!lastLockPackage.equals(""))) {
                    LogUtil.d(TAG, "set last lock package null.");
                    LogUtil.d(TAG, "topPackageName:" + topPackageName + ", lastLockPackage:" + lastLockPackage
                                    + ", getPackageName:" + getPackageName());
                    lastLockPackage = "";
                }
                try {
                    List<LiangApp> apps = DataSupport.where("packageName = ?", topPackageName)
                            .find(LiangApp.class);
                    LiangApp app = apps.get(0);
                    if (apps.size() == 1 && app.isLock() && !app.getPackageName().equals(lastLockPackage)) {
                        showLockView(apps.get(0).getPackageName());
                        LogUtil.d(TAG, "checkLockState: need lock:" + app.getPackageName()
                                + ", last lock package:" + lastLockPackage);

                        lastLockPackage = apps.get(0).getPackageName();

                    }
                } catch (Exception e) {
//                    LogUtil.e(TAG, "checkLockState: " + e.getMessage());
                }
            }
        }
    }

    /*启动锁屏界面*/
    private void showLockView(String packageName) {
        Intent intent = LoginActivity.newIntent(this, packageName);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /*关闭，打开，解锁屏幕广播接收器*/
    public class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                lastLockPackage = "";
            }
        }
    }
}
