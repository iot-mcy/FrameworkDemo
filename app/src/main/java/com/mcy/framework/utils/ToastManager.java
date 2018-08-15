package com.mcy.framework.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 作者 mcy
 * 日期 2018/8/15 13:53
 * Toast提示小工具
 */
public class ToastManager {

    /**
     * ToastShort
     *
     * @param context 上下文
     * @param content 显示内容
     */
    public static void showToastShort(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    /**
     * ToastLong
     *
     * @param context 上下文
     * @param content 显示内容
     */
    public static void showToastLong(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }
}
