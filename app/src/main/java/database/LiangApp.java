package database;

import org.litepal.crud.DataSupport;

/**
 * Created by sohnyi on 2017/3/12.
 */

public class LiangApp extends DataSupport{
    private int id;
    private String package_name;
    private boolean isLock;
    private boolean single_ps;
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

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSingle_ps() {
        return single_ps;
    }

    public void setSingle_ps(boolean single_ps) {
        this.single_ps = single_ps;
    }
}
