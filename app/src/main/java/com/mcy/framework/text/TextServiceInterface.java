package com.mcy.framework.text;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by mcy on 2018/3/23.
 */

public interface TextServiceInterface {

    @POST("/TradeService.svc/GetTradeQuotedPriceByID")
    Observable<GetTradeQuotedPriceByID> getTradeQuotedPriceList(@Body JSONObject object);

    @POST("/TradeService.svc/GetTradeQuotedPriceByID")
    Flowable<GetTradeQuotedPriceByID> getTradeQuotedPriceList2(@Body JSONObject object);

    @POST("/user.svc/getUserByID/{userID}")
    Flowable<String> getUserByUserID(@Path("userID") int name);

    /**
     * 上传单张图片
     */
    @Multipart
    @POST("/user.svc/upload")
    Observable<String> uploadMemberIcon(@Part MultipartBody.Part part);

    @Multipart
    @POST("/user.svc/uploadList")
    Observable<String> uploadAttachments(@Part List<MultipartBody.Part> parts);

    @POST("/user.svc/download/{filename}")
    Observable<ResponseBody> download(@Path("filename") String filename);
}
