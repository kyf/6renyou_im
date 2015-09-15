package com.liurenyou.im;

import android.app.Application;
import android.os.Handler;

/**
 * Created by keyf on 2015/9/15.
 */
public class MyApplication extends Application {

    private Handler myHandler = null;

    public void onCreate(){
        super.onCreate();
    }

    public void setHandler(Handler handler){
        myHandler = handler;
    }

    public Handler getHandler(){
        return myHandler;
    }
}
