package com.sohnyi.liangcare.database;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by sohnyi on 2017/3/18.
 */

public class LiangAppLab {

    private static LiangAppLab sLiangAppLab;


    public static LiangAppLab get() {
        if (sLiangAppLab == null) {
            sLiangAppLab = new LiangAppLab();
        }

        return sLiangAppLab;
    }

    private LiangAppLab() {
    }

    public List<LiangApp> getApps() {

        return DataSupport.findAll(LiangApp.class);
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
