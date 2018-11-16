package com.seeker.tony.myapplication;

import android.app.Application;
import android.content.Context;

import com.meiyou.protocol.ProtocolProxy;


/**
 * @author zhengxiaobin
 * @since 17/7/13
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        ProtocolProxy.getInstance().init(this);
    }

    public static Context getContext() {
        return context;
    }

}
