package com.sohnyi.liangcare.database;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by sohnyi on 2017/3/18.
 */

public class LiangFileLab {

    private static LiangFileLab sFileLab;

    public static LiangFileLab get() {
        if (sFileLab == null) {
            sFileLab = new LiangFileLab();
        }
        return sFileLab;
    }

    private LiangFileLab() {
    }

    /*获取所有数据*/
    public List<LiangFile> getFiles(int type) {
        return DataSupport.where("type = ?" , String.valueOf(type)).find(LiangFile.class);
    }

    /*添加数据*/
    public void addFile(String name, int type, String mimeType,long size, String path, String key) {
        LiangFile file = new LiangFile();
        file.setName(name);
        file.setType(type);
        file.setMimeType(mimeType);
        file.setSize(size);
        file.setPath(path);
        file.setKey(key);
        file.save();
    }

    /*删除数据*/
    public void deleteFile(int id) {
        DataSupport.delete(LiangFile.class, id);
    }
}
