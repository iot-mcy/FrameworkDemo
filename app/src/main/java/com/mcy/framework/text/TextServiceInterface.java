package com.mcy.framework.text;

import com.alibaba.fastjson.JSONObject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by mcy on 2018/3/23.
 */

public interface TextServiceInterface {

    @POST("/TradeService.svc/GetTradeQuotedPriceByID")
    Observable<GetTradeQuotedPriceByID> getTradeQuotedPriceList(@Body JSONObject object);

    @POST("/TradeService.svc/GetTradeQuotedPriceByID")
    Flowable<String> getTradeQuotedPriceList1(@Body JSONObject object);

    @POST("/TradeService.svc/GetTradeQuotedPriceByID")
    Flowable<GetTradeQuotedPriceByID> getTradeQuotedPriceList2(@Body JSONObject object);
}
