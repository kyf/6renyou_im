package com.liurenyou.im;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;


public class MainActivity extends Activity {

    private ProgressDialog pd;

    private Context myContext;

    private WebView mainView;

    private Handler myHandler = new Handler(){

        public void handleMessage(Message msg){
            switch(msg.what){
                case 1001://get token failure
                    pd.dismiss();
                    Toast.makeText(myContext, msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;
                case 1002:
                    mainView = (WebView) findViewById(R.id.MainView);
                    String[] data = (String[]) msg.obj;
                    String deviceToken = data[0];
                    String token = data[1];
                    WebSettings settings = mainView.getSettings();

                    settings.setJavaScriptEnabled(true);
                    settings.setJavaScriptCanOpenWindowsAutomatically(true);
                    settings.setAllowFileAccess(true);// 设置允许访问文件数据
                    settings.setSupportZoom(true);
                    settings.setBuiltInZoomControls(true);
                    settings.setJavaScriptCanOpenWindowsAutomatically(true);
                    settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                    settings.setDomStorageEnabled(true);
                    settings.setDatabaseEnabled(true);

                    settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                    mainView.addJavascriptInterface(new JavaScriptMethods(myContext), "JavaScriptMethods");
                    //mainView.loadUrl("http://m.6renyou.com/android/index?token=" + token);


                    mainView.setWebChromeClient(new WebChromeClient(){
                        @Override
                        public void onProgressChanged(WebView view, int newProgress) {
                            if(newProgress == 100){
                                myHandler.sendEmptyMessage(1003);
                            }
                            super.onProgressChanged(view, newProgress);
                        }
                    });

                    mainView.setWebViewClient(new WebViewClient(){
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            return true;
                        }
                    });

                    mainView.loadUrl("http://m.6renyou.com/android/order?token=" + token);
                case 1003:
                    pd.dismiss();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MyApplication)this.getApplicationContext()).setHandler(myHandler);
        myContext = this;
        pd = ProgressDialog.show(this, "", "connecting ...", true, true);

        XGPushManager.registerPush(this, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                Log.e("TPush", "注册成功，设备token为：" + data);
                Utils.getToken(myContext, true, data.toString());
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.e("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
                Utils.getToken(myContext, false, msg);
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent e){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(mainView.canGoBack()){
                mainView.goBack();
            }else{
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
            return true;
        }

        return super.onKeyDown(keyCode, e);
    }

    public void onPause(){
        Utils.online = "off";
        super.onPause();
    }

    public void onStart(){
        Utils.online = "on";
        super.onStart();
    }

    public void onStop(){
        Utils.online = "off";
        super.onStop();
    }

    public void onResume(){
        Utils.online = "on";
        super.onResume();
    }


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
        public String getOnline(){
            return Utils.online;
        }

        @JavascriptInterface
        public String getDeviceToken(){
            return Utils.deviceToken;
        }



    }

}
