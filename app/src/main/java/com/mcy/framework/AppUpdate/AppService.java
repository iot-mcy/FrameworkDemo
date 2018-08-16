package com.mcy.framework.AppUpdate;

import com.mcy.framework.service.Server;
import com.mcy.framework.utils.ProgressListener;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * 下载apk的服务类
 */
public class AppService {
    public AppService() {
    }

    /**
     * @param progressListener
     * @return
     */
    private static AppServiceInterface serviceInterface(ProgressListener progressListener) {
        synchronized (AppServiceInterface.class) {
            return Server.getService(AppServiceInterface.class, progressListener);
        }
    }

    /**
     * @param path
     * @param progressListener
     * @return
     */
    public static Call<ResponseBody> downloadApk(String path, ProgressListener progressListener) {
        return serviceInterface(progressListener).downloadApk(path);
    }
}
