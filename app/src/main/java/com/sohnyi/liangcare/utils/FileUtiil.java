package com.sohnyi.liangcare.utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by sohnyi on 2017/4/25.
 */

public class FileUtiil {

    public void fileSave(String path) {
        createNewFile(path);

    }

    private File createNewFile(String path) {
        File file = new File(path);
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            Log.d(TAG, "createNewFile: " + e.getMessage());
        }
        return file;
    }
}
