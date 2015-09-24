package com.liurenyou.im;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * Created by keyf on 2015/9/15.
 */
public class MyApplication extends Application {

    private Handler myHandler = null;

    private static Context mContext = null;

    public void onCreate(){
        super.onCreate();
        mContext = this.getApplicationContext();
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
