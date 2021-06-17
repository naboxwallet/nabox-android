package com.duke.dfileselector.util;

import android.os.Environment;
import android.provider.SyncStateContract;

import java.io.File;

/**
 * @author duke
 * @dateTime 2018-09-08 17:02
 * @description
 */
public class FileUtils {

    /**
     * 获取文件的后缀名，不包含 点
     *
     * @param file
     * @return
     */
    public static String getFileSuffixWithOutPoint(File file) {
        if (file.isDirectory()) {
            return null;
        }
        String name = file.getName();
        if ("".equals(name = name.trim()) || !name.contains(".")) {
            return null;
        }
        int index = name.lastIndexOf(".");
        name = name.substring(index + 1);
        if ("".equals(name = name.trim())) {
            return null;
        }
        return name.toLowerCase();
    }

    /**
     * 扩展卡是否可用
     *
     * @return
     */
    public static boolean isExternalStorageOK() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
//    public  static final  String SDCARD_PATH_NABOX = "/mnt/sdcard/Nabox/";
//public  static final String SDCARD_PATH_NABOX = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
    public  static final  String SDCARD_PATH_NABOX =Environment.getExternalStorageDirectory()+"/Nabox/";
    /**
     * 获取扩展卡根目录
     *
     * @return
     */
    public static File getRootFile() {
        if (!isExternalStorageOK()) {
            return null;
        }
        File file = new File(SDCARD_PATH_NABOX);
        if(file.exists()){
            return file;
        }
        return Environment.getExternalStorageDirectory();
    }


    public static File getDefauhtRootFile() {
        if (!isExternalStorageOK()) {
            return null;
        }
        return Environment.getExternalStorageDirectory();
    }

}
