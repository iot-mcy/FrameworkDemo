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

        File file = null;
        String state = android.os.Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
        } else {
            file = new File(context.getCacheDir() + File.separator + fileName);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            Uri contentUri = FileProvider.getUriForFile(context, getFileProviderAuthority(context), file);
//            context.grantUriPermission(context.getPackageName(), contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            ParcelFileDescriptor descriptor = null;
//            try {
//                descriptor = context.getContentResolver().openFileDescriptor(contentUri, "r");
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            FileDescriptor fileDescriptor = descriptor.getFileDescriptor();
//        }

        return file;

    }

    public static Uri getUriForFile(Context context, File file) {
        Uri fileUri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = getUriForFile24(context, file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        context.grantUriPermission(context.getPackageName(), fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        context.grantUriPermission(context.getPackageName(), fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return fileUri;
    }

    private static Uri getUriForFile24(Context context, File file) {
        Uri fileUri = FileProvider.getUriForFile(context,
                context.getPackageName() + ".file_provider",
                file);
        return fileUri;
    }

    /**
     * 获取FileProvider的auth
     */
    private static String getFileProviderAuthority(Context context) {
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

    private static File getDownloadDir(Context context, String name) {
        File downloadDir = null;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            if (name != null) {
                downloadDir = new File(Environment.getExternalStorageDirectory(), "update");
            } else {
                downloadDir = new File(context.getExternalCacheDir(), "update");
            }
        } else {
            downloadDir = new File(context.getCacheDir(), "update");
        }
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }
        return downloadDir;
    }

    /**
     * 将下载的附件写入磁盘
     *
     * @param context
     * @param body
     * @param filename
     * @return
     */
    public static boolean writeResponseBodyToDisk(Context context, ResponseBody body, String filename) {
        try {
            File file = createFile(context, filename);

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