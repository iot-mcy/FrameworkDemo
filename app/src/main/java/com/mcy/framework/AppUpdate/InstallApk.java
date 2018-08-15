package com.mcy.framework.AppUpdate;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;

import java.io.File;

import static com.mcy.framework.utils.FileUtils.getFileProviderAuthority;

/**
 * 作者 mcy
 * 日期 2018/8/15 18:05
 * 正式安装APP的工具类
 */
public class InstallApk {

    public static final int GET_UNKNOWN_APP_SOURCES = 1001;

    /**
     * 调用系统安装器安装apk
     *
     * @param context 上下文
     * @param file    apk文件
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(context, getFileProviderAuthority(context), file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(file);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 检查Android O 应用安装权限
     *
     * @return false 没有权限，需要申请
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean checkOreoInstallPermission(Context context) {
        return context.getPackageManager().canRequestPackageInstalls();
    }

    /**
     * 获取当前app的升级版本号
     *
     * @param context 上下文
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
}
