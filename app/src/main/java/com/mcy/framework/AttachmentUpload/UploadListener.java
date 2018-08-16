package com.mcy.framework.AttachmentUpload;

/**
 * 上传监听回调，目的是拿回上传地址
 */
public interface UploadListener {
    /**
     * 上传成功
     *
     * @param data
     */
    void done(String data);

    /**
     * 上传失败
     *
     * @param throwable
     */
    void error(Throwable throwable);
}
