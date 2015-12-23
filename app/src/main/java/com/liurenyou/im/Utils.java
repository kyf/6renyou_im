package com.liurenyou.im;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Utils {

    public static String token = "";

    public static String deviceToken = "";

    public static String online = "off";//on, off

    public static void calljs(WebView view, String fn){
        if(view != null) {
            view.loadUrl("javascript:remoteMsgCall." + fn + "();");
        }
    }


    public static boolean checkBlueToothState(){
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();

        if (ba == null){
           return false;
        }else if(ba.isEnabled()){
            return true;
        } else {
            return false;
        }
    }


    public static void saveDeviceToken(String deviceToken){
        String sql = "update `appglobal` set `deviceToken` = '" + deviceToken + "' where id = 1";
        DBHelper.execute(sql);
    }

    public static String getDeviceToken(){
        String sql = "select `deviceToken` from `appglobal` where id = 1";
        Cursor cursor = DBHelper.query(sql);
        cursor.moveToFirst();
        if(cursor.getCount() == 0)return "";
        return cursor.getString(cursor.getColumnIndex("deviceToken"));
    }


    public static void getToken(Context context, boolean status, String deviceToken){
        Message msg = Message.obtain();
        if (!status) {
            msg.what = 1001;
            Log.e("6renyou", "bind failure");
            msg.obj = deviceToken;
            ((MyApplication) context.getApplicationContext()).getHandler().sendMessage(msg);
            return;
        }

        try {
            String token = "";
            Utils.token = token;
            Utils.deviceToken = deviceToken;
        }catch(Exception e){
            Log.e("6renyou", e.getMessage());
            msg.obj = e.getMessage();
            msg.what = 1001;
            ((MyApplication) context.getApplicationContext()).getHandler().sendMessage(msg);
        }
    }

}
