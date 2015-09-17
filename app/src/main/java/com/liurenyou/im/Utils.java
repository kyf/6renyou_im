package com.liurenyou.im;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Message;
import android.util.Log;

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
            /*
            String uri = "http://im1.6renyou.com:8989/auth";
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(uri);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("deviceid", deviceToken));
            post.setEntity(new UrlEncodedFormEntity(nvps));
            HttpResponse res = client.execute(post);
            String body = EntityUtils.toString(res.getEntity());
            JSONObject obj = new JSONObject(body);

            if(obj.getString("status").equals("ok")){

                */

                String token = "";//obj.getString("token");
                msg.what = 1002;
                Utils.token = token;
                Utils.deviceToken = deviceToken;
                msg.obj = new String[]{deviceToken, token};
            /*
            }else{
                msg.obj = obj.getString("msg");
                msg.what = 1001;
            }
            */


        }catch(Exception e){
            Log.e("6renyou", e.getMessage());
            msg.obj = e.getMessage();
            msg.what = 1001;
        }finally{
            ((MyApplication) context.getApplicationContext()).getHandler().sendMessage(msg);
        }
    }

}
