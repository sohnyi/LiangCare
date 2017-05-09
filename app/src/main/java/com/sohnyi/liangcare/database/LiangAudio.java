package com.sohnyi.liangcare.database;

import org.litepal.crud.DataSupport;

/**
 * Created by sohnyi on 2017/5/9.
 */

public class LiangAudio extends DataSupport {
    private int id;
    private String fileName;
    private String fileType;
    private int fileSize;
    private String filePath;
}
