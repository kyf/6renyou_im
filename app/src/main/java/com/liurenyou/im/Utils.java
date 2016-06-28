package com.liurenyou.im;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.WebView;

import com.tencent.mm.algorithm.MD5;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
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

    public static String getUUID(){
        Context context = MyApplication.getContext();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = tm.getDeviceId();
        return md5(deviceid);
    }

    public static String md5(String val){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(val.getBytes());
            byte[] result = md.digest();

            StringBuffer stringBuffer = new StringBuffer();
            for(byte b:result){
                stringBuffer.append(String.format("%x", b));
            }
            return Constants.UUID_PREFIX + stringBuffer.toString();
        }catch(NoSuchAlgorithmException e){
            return Constants.UUID_PREFIX + val;
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
        if(cursor.getCount() == 0)return getUUID();
        String token = cursor.getString(cursor.getColumnIndex("deviceToken"));
        if(token == null || token.equals("")){
            return getUUID();
        }else{
            return token;
        }
    }


    public static void registerDevicetoken(Map<String, String> params){
        HttpClient client = new DefaultHttpClient();
        HttpPost httpost = new HttpPost("http://im1.6renyou.com:3030/android/register");
        List<NameValuePair> nvps = new ArrayList <NameValuePair>();

        Set<String> keySet = params.keySet();
        for(String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }

        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String body = null;
        HttpResponse response = null;

        try {
            response = client.execute(httpost);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpEntity entity = response.getEntity();

        try {
            body = EntityUtils.toString(entity);
            //Log.e("/android/register", body);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.getConnectionManager().shutdown();
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
