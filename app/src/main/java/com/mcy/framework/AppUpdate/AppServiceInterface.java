package com.mcy.framework.AppUpdate;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AppServiceInterface {
    @GET("{path}")
    Call<ResponseBody> downloadApk(@Path("path") String path);
}
