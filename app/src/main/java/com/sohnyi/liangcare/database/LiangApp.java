package com.sohnyi.liangcare.database;

import org.litepal.crud.DataSupport;

/**
 * Created by sohnyi on 2017/3/12.
 */

public class LiangApp extends DataSupport{
    private int id;
    private String packageName;
    private boolean isLock;
    private boolean inPass;
    private String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String package_name) {
        this.packageName = package_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isInpass() {
        return inPass;
    }

    public void setInpass(boolean single_ps) {
        this.inPass = single_ps;
    }
}
