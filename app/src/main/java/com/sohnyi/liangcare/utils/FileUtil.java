package com.sohnyi.liangcare.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

import static android.content.ContentValues.TAG;
import static com.sohnyi.liangcare.database.MyConstant.SIZE_MAX;

/**
 * Created by sohnyi on 2017/4/25.
 */

public class FileUtil {

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


    private void moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {
            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + inputFile).delete();


        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    public static void deleteFile(String filePath) {
        try {
            // delete the original file
            new File(filePath).delete();
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    private static void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    public static String sizeFormat(long size) {
        float curSize = size;
        int grade = 0;
        String unit;
        while (curSize > SIZE_MAX) {
            curSize /= SIZE_MAX;
            grade++;
        }
        switch (grade) {
            case 0:
                unit = " B";
                break;
            case 1:
                unit = " KB";
                break;
            case 2:
                unit = " MB";
                break;
            case 3:
                unit =  " GB";
                break;
            case 4:
                unit =  " TB";
                break;
            default:
                unit =  " UNKNOW";
        }
        return new DecimalFormat("0.0").format(curSize) + unit;
    }
}
