package com.mcy.framework;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import okio.ForwardingSource;

/**
 * @Description: 描述
 * @AUTHOR 刘楠  Create By 2016/10/27 0027 15:56
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

    public static void writeFile2Disk(ResponseBody response, File file, HttpCallBack httpCallBack) {

        InputStream inputStream = null;
        OutputStream outputStream = null;
//        new ForwardingSource(response.source())
        try {
            long currentLength = 0;
            long totalLength = response.contentLength();

            inputStream = response.byteStream();
            outputStream = new FileOutputStream(file);

            byte[] buff = new byte[2048];

            while (true) {
                int read = inputStream.read(buff);
                if (read == -1) {
                    break;
                }
                outputStream.write(buff, 0, read);

                currentLength += read;
                httpCallBack.onLoading(currentLength, totalLength);
            }
            outputStream.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            httpCallBack.onError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            httpCallBack.onError(e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    httpCallBack.onError(e.getMessage());
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    httpCallBack.onError(e.getMessage());
                }
            }
        }
    }
}