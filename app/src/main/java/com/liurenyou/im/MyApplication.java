package com.liurenyou.im;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.ebudiu.budiu.sdk.SDKInitializer;

/**
 * Created by keyf on 2015/9/15.
 */
public class MyApplication extends Application {

    private Handler myHandler = null;

    private static Context mContext = null;

    public void onCreate(){
        super.onCreate();
        mContext = this.getApplicationContext();

        SDKInitializer.initialize(this);
    }

    public void setHandler(Handler handler){
        myHandler = handler;
    }

    public Handler getHandler(){
        return myHandler;
    }

    public static Context getContext(){
        return mContext;
    }
}
