package com.nuls.naboxpro.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    /**
     * 遍历获取某目录下制定类型的所有文件
     * @param filePath
     * @param type
     * @return
     */
    public  List<String> getFileDir(String filePath,String type) {
        List<String> picList = new ArrayList<String>();
        try{
            File f = new File(filePath);
            File[] files = f.listFiles();// 列出所有文件
            // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (checkIsImageFile(file.getPath(),type)) {
                    picList.add(file.getPath());
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        // 返回得到的图片列表
        return picList;
    }
    // 检查扩展名，得到图片格式的文件
    private boolean checkIsImageFile(String fName,String type) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals(type)) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }

        return isImageFile;

    }
    /**
     * 如果文件不存在，就创建文件
     *
     * @param path 文件路径
     * @return
     */
    public static String createIfNotExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return path;
    }

    /**
     * 向文件中写入数据
     *
     * @param filePath
     *            目标文件全路径
     * @param data
     *            要写入的数据
     * @return true表示写入成功  false表示写入失败
     */
    public static boolean writeBytes(String filePath, byte[] data) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(data);
            fos.close();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * 从文件中读取数据
     *
     * @param file
     * @return
     */
    public static byte[] readBytes(String file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            int len = fis.available();
            byte[] buffer = new byte[len];
            fis.read(buffer);
            fis.close();
            return buffer;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;

    }



//    public static  Integer getKeystoreVersion(String filePath){
//
//        if(!TextUtils.isEmpty(filePath)){
//            String fileContent = FileUtil.readString(filePath,"UTF-8");
//            if (TextUtils.isEmpty(fileContent)){
//                return ImportKeystoreFragment.TYPE_VERSION2;
//            }
//            try {
//                JSONObject jsonObject = new JSONObject(fileContent);
//                if(jsonObject.getString("address")!=null){
//                    String address = jsonObject.getString("address");
//                    if(address.substring(0,2).equals("Ns")){
//                        //这是1.0版本
//                        return ImportKeystoreFragment.TYPE_VERSION1;
//                    }
//                    return ImportKeystoreFragment.TYPE_VERSION2;
//
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        return ImportKeystoreFragment.TYPE_VERSION2;
//    }

    /**
     * 向文件中写入字符串String类型的内容
     *
     * @param filePath
     *            文件路径
     * @param content
     *            文件内容
     * @param charset
     *            写入时候所使用的字符集
     */
    public static void writeString(String filePath, String content, String charset) {
        if(TextUtils.isEmpty(charset)){
           charset =  "UTF-8";
        }
        createIfNotExist(filePath);
        try {
            byte[] data = content.getBytes(charset);
            boolean isWrite = writeBytes(filePath, data);
            Log.i("isWrite","*************"+isWrite);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * 从文件中读取数据，返回类型是字符串String类型
     *
     * @param file
     *            文件路径
     * @param charset
     *            读取文件时使用的字符集，如utf-8、GBK等
     * @return
     */
    public static String readString(String file, String charset) {
        byte[] data = readBytes(file);
        String ret = null;

        try {
            ret = new String(data, charset);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ret;


    }


    /**
     * 获取文件选择器选中的文件路径
     * @param context
     * @param uri
     * @return
     */
    public static String getPath(Context context, Uri uri) {

        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA }, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return path;
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();
            return path;
        } else {
            //Log.i(TAG, "Uri Scheme:" + uri.getScheme());
        }
        return null;
    }




    public static String getFilePathFromContentUri(Uri selectedVideoUri,
                                                   Activity context) {
        String filePath = "";
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

//      Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
//      也可用下面的方法拿到cursor
        Cursor cursor = context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            try {
                //4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
                if (Integer.parseInt(Build.VERSION.SDK) < 14) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.e("转换地址", "error:" + e);
            }
        }
        return filePath;
    }



    public static String getFPUriToPath(Context context, Uri uri) {
        try {
            List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
            if (packs != null) {
                String fileProviderClassName = FileProvider.class.getName();
                for (PackageInfo pack : packs) {
                    ProviderInfo[] providers = pack.providers;
                    if (providers != null) {
                        for (ProviderInfo provider : providers) {
                            if (uri.getAuthority().equals(provider.authority)) {
                                if (provider.name.equalsIgnoreCase(fileProviderClassName)) {
                                    Class<FileProvider> fileProviderClass = FileProvider.class;
                                    try {
                                        Method getPathStrategy = fileProviderClass.getDeclaredMethod("getPathStrategy", Context.class, String.class);
                                        getPathStrategy.setAccessible(true);
                                        Object invoke = getPathStrategy.invoke(null, context, uri.getAuthority());
                                        if (invoke != null) {
                                            String PathStrategyStringClass = FileProvider.class.getName() + "$PathStrategy";
                                            Class<?> PathStrategy = Class.forName(PathStrategyStringClass);
                                            Method getFileForUri = PathStrategy.getDeclaredMethod("getFileForUri", Uri.class);
                                            getFileForUri.setAccessible(true);
                                            Object invoke1 = getFileForUri.invoke(invoke, uri);
                                            if (invoke1 instanceof File) {
                                                String filePath = ((File) invoke1).getAbsolutePath();
                                                return filePath;
                                            }
                                        }
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 判断文件类型是否是keystore
     * @param filename 文件名
     * @return
     */
    public static boolean isKeystore(String filename) {
        if (filename.contains(".")) {
            String type = filename.substring(filename.lastIndexOf("."));
            if(type.equalsIgnoreCase(".keystore")){
                return true;
            }
        }
        return false;
    }


//    /** 文件类型 */
//    public static int getFileType(String filename){
//
//
//        if(filename.contains(".")){
//
//
//            String type = filename.substring(filename.lastIndexOf("."));
//
//            //Utils.MyLog("file: " + name + "   type: " + type);
//            if(type.equalsIgnoreCase(".doc") || type.equalsIgnoreCase(".docx")){
//
//                return TYPE_DOC;
//
//            }else if(type.equalsIgnoreCase(".xls") || type.equalsIgnoreCase(".xlsx")){
//
//                return TYPE_XLS;
//
//            }else if(type.equalsIgnoreCase(".ppt") || type.equalsIgnoreCase(".pptx")){
//
//                return TYPE_PPT;
//
//            }else if(type.equalsIgnoreCase(".pdf")){
//
//                return TYPE_PDF;
//
//            }else if(type.equalsIgnoreCase(".txt")){
//
//                return TYPE_TXT;
//
//            }else{
//
//                for (String format : MIME_TYPE_PHOTO) {
//                    if (format.equalsIgnoreCase(type)) {
//                        return TYPE_PICTURE;
//                    }
//                }
//
//                for (String format : MIME_TYPE_VIDEO_ALL) {
//                    if (format.equalsIgnoreCase(type)) {
//                        return TYPE_VIDEO;
//                    }
//                }
//
//                for (String format : MIME_TYPE_MUSIC) {
//                    if (format.equalsIgnoreCase(type)) {
//                        return TYPE_AUDIO;
//                    }
//                }
//
//            }
//
//        }else{
//
//            return TYPE_FILE;
//
//        }
//
//        return TYPE_FILE;
//
//    }
//
//
//
//    //文件类型
//    public static final int TYPE_FOLDER = 0;
//    public static final int TYPE_FILE = 1;
//    //public static final int TYPE_PICTURE = 0;
//    public static final int TYPE_VIDEO = 2;
//    public static final int OPEN_FILE = 10;
//    public static final int TYPE_AUDIO = 13;
//    public static final int TYPE_PICTURE = 14;
//    public static final int TYPE_DOC = 21;
//    public static final int TYPE_XLS = 22;
//    public static final int TYPE_PPT = 23;
//    public static final int TYPE_PDF = 24;
//    public static final int TYPE_TXT = 25;
//
//
//    /** 视频格式 */
//    public static final String[] MIME_TYPE_VIDEO_ALL = { ".3gp", ".3gpp", ".divx",
//            ".h264", ".avi", ".m2ts", ".mkv", ".mov", ".mp2", ".mp4", ".mpg",
//            ".mpeg", ".wmv", ".ts", ".tp" , ".vob",
//            ".flv", ".vc1", ".m4v", ".f4v", ".asf", ".lst", ".lsf", ".lsx",
//            ".mng", ".asx", ".wm", ".wmx", ".wvx", ".movie", ".3g2", ".dl",
//            ".dif", ".dv", ".fli", ".qt", ".mxu",".webm", "mkv", "rmvb"};
//
//    /** 音频格式 */
//    public static final String[] MIME_TYPE_MUSIC = { ".mp3", ".wma", ".m4a",
//            ".aac", ".ape", ".ogg", ".flac", ".alac", ".wav", ".mid", ".xmf",
//            ".mka", ".pcm", ".adpcm", ".snd", ".midi", ".kar", ".mpga",
//            ".mpega", ".mp2", ".m3u", ".sid", ".aif", ".aiff", ".aifc", ".gsm",
//            ".m3u", ".wax", ".ra", ".ram", ".pls", ".sd2","amr","wv","mmf","m4r"};
//
//
//    /** 图片格式 */
//    public static final String[] MIME_TYPE_PHOTO = { ".jpg", ".jpeg", ".bmp",
//            ".tif", ".tiff", ".png", ".gif", ".giff", ".jfi", ".jpe", ".jif",
//            ".jfif", ".cur", ".ico", ".ief", ".jpe", ".pcx", ".svg", ".svgz",
//            ".djvu", ".djv", ".wbmp", ".ras", ".cdr", ".pat", ".cdt", ".cpt",
//            ".art", ".jng", ".psd", ".pnm", ".pbm", ".pgm", ".ppm", ".rgb",
//            ".xbm", ".xpm", ".xwd" };

    /**
     * 复制到剪贴板
     *
     * @param context 上下文
     * @param text    要复制的内容
     */
    public static void copyToClipboard(Context context, String text) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("text", text));

    }
    }





