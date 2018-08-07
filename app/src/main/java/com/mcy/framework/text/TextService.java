package com.mcy.framework.text;

import com.alibaba.fastjson.JSONObject;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by mcy on 2018/3/23.
 */

public interface TextService {

    @POST("/TradeService.svc/GetTradeQuotedPriceByID")
    Observable<String> getTradeQuotedPriceList(@Body JSONObject object);
}
