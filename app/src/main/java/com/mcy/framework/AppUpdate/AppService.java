package com.mcy.framework.AppUpdate;

import com.mcy.framework.service.Server;
import com.mcy.framework.utils.ProgressListener;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class AppService {
    public AppService() {
    }

    private static AppServiceInterface serviceInterface(ProgressListener progressListener) {
        synchronized (AppServiceInterface.class) {
            return Server.getService(AppServiceInterface.class, progressListener);
        }
    }

    public static Call<ResponseBody> downloadApk(String path, ProgressListener progressListener) {
        return serviceInterface(progressListener).downloadApk(path);
    }
}
