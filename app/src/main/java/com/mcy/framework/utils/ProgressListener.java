package com.mcy.framework.utils;

/**
 * 作者 mcy
 * 日期 2018/8/13 18:01
 */
public interface ProgressListener {

    /**
     * @param progress 已经下载或上传字节数
     * @param total    总字节数
     * @param done     是否完成
     */
    void onProgress(long progress, long total, boolean done);
}
