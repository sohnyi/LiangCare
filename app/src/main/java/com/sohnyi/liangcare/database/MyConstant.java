package com.sohnyi.liangcare.database;

/**
 * Created by sohnyi on 2017/5/9.
 */

public class MyConstant {
    public static final String MYPACKAGENAME = "com.sohnyi.liangcare";

    /*文件类型*/
    public static final int TYPE_AUDIO = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_DOC = 3;

    /*key-value*/
    public static final String FILE_TYPE = "com.sohnyi.liangcare.filetype";

    /*请求码*/
    public static final int FILE_ADD_ACTION_REQUEST_CODE = 1;

    /*加解密*/
    public static final String ENCRY_ALGORITM = "AES";
    public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    public static final int ENCRY_FLAG = 0;
    public static final int DECRY_FLAG = 1;

    /*文档文件类型*/
    public static final String[] DOCUMENT_TYPES ={
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/pdf",
            "application/octet-stream",
            "text/*"
    };

    /*SecCabActivity*/
    public static final String CURRENT_TYPE = "current_type";
    public static final int SIZE_MAX = 1024;

}
