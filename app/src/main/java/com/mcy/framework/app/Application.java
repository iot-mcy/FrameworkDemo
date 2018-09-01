package com.mcy.framework.app;

import com.mcy.framework.user.User;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        User.getInstance(this);
    }

}
