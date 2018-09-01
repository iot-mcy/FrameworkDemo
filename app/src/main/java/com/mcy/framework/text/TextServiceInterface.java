package com.mcy.framework.text;

import com.alibaba.fastjson.JSONObject;
import com.mcy.framework.baseEntity.ResponseEntity;
import com.mcy.framework.user.User;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
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

    @POST("/user.svc/login")
    Flowable<ResponseEntity<User>> login(@Body JSONObject object);
    @POST("/user.svc/logout")
    Flowable<String> logout();
}
