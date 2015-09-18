package com.liurenyou.im;

import android.content.Context;
import android.os.Build;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by keyf on 2015/9/18.
 */

public class JavaScriptMethods {

    private Context mContext;


    public JavaScriptMethods(Context context){
        mContext = context;
    }

    @JavascriptInterface
    public void alert(String content){
        Toast.makeText(mContext, content, Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    public String getToken(){
        return Utils.token;
    }

    @JavascriptInterface
    public void shareTextToFriend(String content){
        WeixinShareManager wsm = WeixinShareManager.getInstance(mContext);
        wsm.shareByWeixin(wsm.new ShareContentText(content),
                WeixinShareManager.WEIXIN_SHARE_TYPE_TALK);
    }

    @JavascriptInterface
    public void shareTextToCircle(String content){
        WeixinShareManager wsm = WeixinShareManager.getInstance(mContext);
        wsm.shareByWeixin(wsm.new ShareContentText(content),
                WeixinShareManager.WEIXIN_SHARE_TYPE_FRENDS);
    }


    @JavascriptInterface
    public void sharePicToFriend(String url){
        WeixinShareManager wsm = WeixinShareManager.getInstance(mContext);
        wsm.shareByWeixin(wsm.new ShareContentPic(url),
                WeixinShareManager.WEIXIN_SHARE_TYPE_TALK);
    }

    @JavascriptInterface
    public void sharePicToCircle(String url){
        WeixinShareManager wsm = WeixinShareManager.getInstance(mContext);
        wsm.shareByWeixin(wsm.new ShareContentPic(url),
                WeixinShareManager.WEIXIN_SHARE_TYPE_FRENDS);
    }

    @JavascriptInterface
    public void sharePicToCircle1(int res){
        WeixinShareManager wsm = WeixinShareManager.getInstance(mContext);
        wsm.shareByWeixin(wsm.new ShareContentPic(res),
                WeixinShareManager.WEIXIN_SHARE_TYPE_FRENDS);
    }


    @JavascriptInterface
    public String getOnline(){
        return Utils.online;
    }

    @JavascriptInterface
    public String getDeviceToken(){
        return Utils.deviceToken;
    }

    @JavascriptInterface
    public String getDeviceInfo(){
        Map<String, String> info = new HashMap<String, String>();
        TimeZone tz = TimeZone.getDefault();

        String name = Build.USER;//设备名称，用户在设置中自定义的名称
        String timezone = tz.getID();//时区
        String appversion = "1.0";//app版本
        String clientid = Utils.deviceToken;//客户端id
        String systemversion = Build.VERSION.RELEASE  ;//系统版本，7.0+
        String systemname = "Android";//系统名称，一般为iPhone OS
        String devicemodel = Build.MODEL;//设备型号
        String country = "zh";//国家
        String language = mContext.getResources().getConfiguration().locale.getLanguage();// 语言

        info.put("name", name);
        info.put("timezone", timezone);
        info.put("appversion", appversion);
        info.put("clientid", clientid);
        info.put("systemversion", systemversion);
        info.put("systemname", systemname);
        info.put("devicemodel", devicemodel);
        info.put("country", country);
        info.put("language", language);


        JSONObject json = new JSONObject(info);
        return json.toString();
    }

}

