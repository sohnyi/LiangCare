package com.sohnyi.liangcare.database;

import com.sohnyi.liangcare.utils.LogUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import static android.content.ContentValues.TAG;

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

    /*获取所有数据*/
    public List<LiangApp> getApps() {
        return DataSupport.order("isLock").find(LiangApp.class);
    }

    /*添加数据*/
    public void addApp(String packageName) {
        LiangApp app = getApp(packageName);
        if (app == null) {
            app = new LiangApp();
            app.setPackageName(packageName);
            app.setLock(false);
            app.setInpass(false);
            app.setPassword(null);
            app.save();
            LogUtil.d(TAG, "addApp: " + packageName);
        }
    }

    /*删除数据*/
    public void deleteApp(String packageName) {
        LiangApp app = getApp(packageName);
        if (app != null) {
            try {
                DataSupport.delete(LiangApp.class ,app.getId());
                LogUtil.d(TAG, "deleteApp: " + packageName);
            } catch (Exception e) {
                LogUtil.e(TAG, "deleteApp: " + e.getMessage());
            }
        }
    }

    public static LiangApp getApp(String packageName) {
        List<LiangApp> apps = DataSupport.where("packageName = ?", packageName)
                .find(LiangApp.class);
        if (apps.size() == 1) {
            return apps.get(0);
        } else {
            return null;
        }
    }
}
