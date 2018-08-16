package com.mcy.framework.AttachmentUpload;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * 上传附件的接口
 */
public interface AttachmentUploadServiceInterface {
    /**
     * 上传单个附件
     */
    @Multipart
    @POST("/user.svc/uploadAttachment")
    Observable<String> uploadAttachment(@Part MultipartBody.Part part);

    /**
     * 上传多个附件
     *
     * @param parts
     * @return
     */
    @Multipart
    @POST("/user.svc/uploadAttachments")
    Observable<String> uploadAttachments(@Part List<MultipartBody.Part> parts);
}
