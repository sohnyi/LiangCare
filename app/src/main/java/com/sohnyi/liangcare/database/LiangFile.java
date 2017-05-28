package com.sohnyi.liangcare.database;

import org.litepal.crud.DataSupport;

/**
 * Created by sohnyi on 2017/5/9.
 */

public class LiangFile extends DataSupport {
    private int id;
    private String name;
    private int type;
    private String mimeType;
    private long size;
    private String path;
    private String key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
