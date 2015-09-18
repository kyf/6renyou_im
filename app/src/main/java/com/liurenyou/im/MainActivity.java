package com.liurenyou.im;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.liurenyou.im.uikit.MMAlert;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class MainActivity extends Activity {

    private ProgressDialog pd;

    private Context myContext;

    private WebView mainView;

    private IWXAPI api;

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
                    JavaScriptMethods jsm = new JavaScriptMethods(myContext);
                    mainView.addJavascriptInterface(jsm, "JavaScriptMethods");

                    mainView.setWebChromeClient(new WebChromeClient(){

                        //For Android 4.1
                        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {

                            mUploadMessage1 = uploadMsg;
                            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                            i.addCategory(Intent.CATEGORY_OPENABLE);
                            i.setType("image/*");
                            MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), MainActivity.FILECHOOSER_RESULTCODE);

                        }

                        protected void openFileChooser(ValueCallback<Uri> uploadMsg)
                        {
                            mUploadMessage1 = uploadMsg;
                            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                            i.addCategory(Intent.CATEGORY_OPENABLE);
                            i.setType("image/*");
                            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
                        }

                        // For 3.0+ Devices (Start)
                        protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
                        {
                            mUploadMessage1 = uploadMsg;
                            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                            i.addCategory(Intent.CATEGORY_OPENABLE);
                            i.setType("image/*");
                            startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
                        }

                        //For Android 5.0
                        public boolean  onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                          FileChooserParams fileChooserParams){
                            if (MainActivity.this.mUploadMessage != null) {
                                MainActivity.this.mUploadMessage.onReceiveValue(null);
                                MainActivity.this.mUploadMessage = null;
                            }

                            MainActivity.this.mUploadMessage = filePathCallback;
                            Intent intent = fileChooserParams.createIntent();
                            try {
                                MainActivity.this.startActivityForResult(intent, FILECHOOSER_RESULTCODE);
                            } catch (ActivityNotFoundException e) {
                                MainActivity.this.mUploadMessage = null;
                                Toast.makeText(MainActivity.this, "Cannot open file chooser", Toast.LENGTH_LONG).show();
                                return false;
                            }

                            return true;

                        }

                        @Override
                        public void onProgressChanged(WebView view, int newProgress) {
                            if(newProgress == 100){
                                myHandler.sendEmptyMessage(1003);
                                /*****send message to wx ****************/
/*
                                final EditText editor = new EditText(MainActivity.this);
                                editor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                editor.setText(R.string.send_text_default);

                                MMAlert.showAlert(MainActivity.this, "send text", editor, getString(R.string.app_share), getString(R.string.app_cancel), new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String text = editor.getText().toString();
                                        if (text == null || text.length() == 0) {
                                            return;
                                        }


                                        WXTextObject textObj = new WXTextObject();
                                        textObj.text = text;

                                        WXMediaMessage msg = new WXMediaMessage();
                                        msg.mediaObject = textObj;
                                        msg.description = text;


                                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                                        req.transaction = buildTransaction("text");
                                        req.message = msg;
                                        req.scene = SendMessageToWX.Req.WXSceneTimeline;


                                        boolean result = api.sendReq(req);
                                        Toast.makeText(MainActivity.this, "" + result, Toast.LENGTH_SHORT).show();
                                    }
                                }, null);
*/
                                        /*****************end*******************/
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
                    mainView.loadUrl("http://m.6renyou.com/android/index");
                case 1003:
                    pd.dismiss();
            }
        }

    };

    private ValueCallback<Uri[]> mUploadMessage;
    private ValueCallback<Uri> mUploadMessage1;
    private final static int FILECHOOSER_RESULTCODE = 1;

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null != mUploadMessage1) {
                Uri result = intent == null || resultCode != RESULT_OK ? null
                        : intent.getData();
                mUploadMessage1.onReceiveValue(result);
                mUploadMessage1 = null;
            }

            if(null != mUploadMessage){
                mUploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                mUploadMessage = null;
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
        setContentView(R.layout.activity_main);
        ((MyApplication)this.getApplicationContext()).setHandler(myHandler);
        myContext = this;
        pd = ProgressDialog.show(this, "", "connecting ...", true, true);


        api.registerApp(Constants.APP_ID);


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
        public void shareTextToFriend(String content){
            WeixinShareManager wsm = WeixinShareManager.getInstance(MainActivity.this);
            wsm.shareByWeixin(wsm.new ShareContentText(content),
                    WeixinShareManager.WEIXIN_SHARE_TYPE_TALK);
        }

        @JavascriptInterface
        public void shareTextToCircle(String content){
            WeixinShareManager wsm = WeixinShareManager.getInstance(MainActivity.this);
            wsm.shareByWeixin(wsm.new ShareContentText(content),
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
            String language = getResources().getConfiguration().locale.getLanguage();// 语言

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

}
