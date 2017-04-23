package com.sohnyi.liangcare.database;

import android.content.Context;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.List;

/**
 * Created by sohnyi on 2017/3/18.
 */

public class LiangAppLab {

    private static final String TAG = "ApppLab";
    private static final String PACKAGENAME = "packageName";

    private static LiangAppLab sLiangAppLab;
    private Context mContext;

    private List<LiangApp> mLiangApps;

    public static LiangAppLab get(Context context) {
        if (sLiangAppLab == null) {
            sLiangAppLab = new LiangAppLab(context);
        }

        return sLiangAppLab;
    }

    private LiangAppLab(Context context) {
        mContext = context.getApplicationContext();
        Connector.getDatabase();
    }

    public List<LiangApp> getApps() {

        List<LiangApp> apps = DataSupport.findAll(LiangApp.class);

        return apps;
    }



    public void addApp(String packageName) {
        LiangApp app = new LiangApp();
        app.setPackageName(packageName);
        app.setLock(false);
        app.setInpass(false);
        app.setPassword(null);
        app.save();
    }
}
