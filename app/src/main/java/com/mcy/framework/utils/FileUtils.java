package com.mcy.framework.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

/**
 * 作者 mcy
 * 日期 2018/8/14 10:34
 * 文件处理工具
 */
public class FileUtils {

    public static File createFile(Context context, String fileName) {

        File filePath;
        String state = android.os.Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            filePath = new File(Environment.getExternalStorageDirectory() + File.separator + "mcy");
        } else {
            filePath = new File(context.getCacheDir() + File.separator + "mcy");
        }

        if (!filePath.exists()) {
            filePath.mkdirs();
        }

        File file = new File(filePath, fileName);
        return file;

    }

    /**
     * 获取FileProvider的auth
     */
    public static String getFileProviderAuthority(Context context) {
        try {
            for (ProviderInfo provider : context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PROVIDERS).providers) {
                if (FileProvider.class.getName().equals(provider.name) && provider.authority.endsWith(".file_provider")) {
                    return provider.authority;
                }
            }
        } catch (PackageManager.NameNotFoundException ignore) {
        }
        return "";
    }

    /**
     * 将下载的附件写入磁盘
     *
     * @param body
     * @param file
     * @return
     */
    public static boolean writeResponseBodyToDisk(ResponseBody body, File file) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


}