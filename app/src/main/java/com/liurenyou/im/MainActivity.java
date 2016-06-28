package com.liurenyou.im;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;



import com.liurenyou.im.widget.MyLoading;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class MainActivity extends Activity {

    private MyLoading pd;

    private Context myContext;

    private WebView mainView;

    private IWXAPI api;

    private boolean hasLoaded = false;


    private String loadURL = Constants.homePage;


    private LinearLayout ErrorPanel;

    private Button ReloadBt;

    private Handler myHandler = new Handler(){

        public void handleMessage(Message msg){
            switch(msg.what){
                case 1001://get token failure
                    pd.dismiss();
                    Toast.makeText(myContext, msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;
                case 1002:
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
                    //settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                    settings.setCacheMode(WebSettings.LOAD_DEFAULT);
                    settings.setDefaultTextEncodingName("UTF-8");
                    settings.setDomStorageEnabled(true);
                    settings.setDatabaseEnabled(true);

                    settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                    JavaScriptMethods jsm = new JavaScriptMethods(myContext);
                    mainView.addJavascriptInterface(jsm, "JavaScriptMethods");

                    mainView.setWebChromeClient(new WebChromeClient() {

                        //For Android 4.1
                        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {

                            mUploadMessage1 = uploadMsg;
                            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                            i.addCategory(Intent.CATEGORY_OPENABLE);
                            i.setType("image/*");
                            MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), MainActivity.FILECHOOSER_RESULTCODE);

                        }

                        protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
                            mUploadMessage1 = uploadMsg;
                            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                            i.addCategory(Intent.CATEGORY_OPENABLE);
                            i.setType("image/*");
                            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
                        }

                        // For 3.0+ Devices (Start)
                        protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                            mUploadMessage1 = uploadMsg;
                            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                            i.addCategory(Intent.CATEGORY_OPENABLE);
                            i.setType("image/*");
                            startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
                        }

                        //For Android 5.0
                        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                                         FileChooserParams fileChooserParams) {
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
                            if (newProgress == 100) {
                                myHandler.sendEmptyMessage(1003);

                                /*用户授权 begin*/
                                //WeixinShareManager shareManager = WeixinShareManager.getInstance(myContext);
                                //shareManager.auth();
                                /*end*/
                            }
                            super.onProgressChanged(view, newProgress);
                        }
                    });

                    mainView.setWebViewClient(new WebViewClient(){

                        @Override
                        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                            //view.loadUrl("file:///android_asset/www/error.html");
                            view.setVisibility(View.GONE);
                            view.loadData("", "text/html", "UTF-8");
                            ErrorPanel.setVisibility(View.VISIBLE);

                        }


                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            return true;
                        }
                    });

                    String loadurl = getIntent().getStringExtra("loadurl");
                    if(loadurl != null && !loadurl.equalsIgnoreCase("")){
                        loadURL = loadurl;
                    }

                    mainView.loadUrl(loadURL);
                    break;
                case 1003:
                    mainView.setVisibility(View.VISIBLE);
                    pd.dismiss();
                    break;
                case 1004:
                    String obj = (String) msg.obj;
                    try {
                        JSONObject jsonObject = new JSONObject(obj);
                        com.liurenyou.im.widget.AlertDialog iosDialog = new com.liurenyou.im.widget.AlertDialog(MainActivity.this);
                        iosDialog.builder();
                        iosDialog.setTitle("版本升级");
                        iosDialog.setMsg(jsonObject.getString("desc"), Gravity.LEFT);
                        iosDialog.setCancelable(true);
                        iosDialog.setPositiveButton("", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(myContext, "已转入后台下载", Toast.LENGTH_LONG).show();
                                UpdateApk(Constants.apkURL);
                            }
                        });

                        iosDialog.setNegativeButton("", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                        iosDialog.show();
                    }catch(Exception e){
                        Log.e("6renyou", e.getMessage());
                    }
                    break;
            }
        }

    };

    private ValueCallback<Uri[]> mUploadMessage;
    private ValueCallback<Uri> mUploadMessage1;
    private final static int FILECHOOSER_RESULTCODE = 1;

    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {

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


    private boolean isFirst(){
        int versionCode = getVersionCode(this);

        String sql = "select `isfirst` from `appglobal` where id = 1";
        Cursor cursor = DBHelper.query(sql);
        cursor.moveToFirst();


        if(cursor.getCount() == 0){
            sql = "insert into `appglobal`(`isfirst`) values(" + versionCode + ")";
            DBHelper.execute(sql);
            return true;
        }


        int isfirst = cursor.getInt(cursor.getColumnIndex("isfirst"));

        if(isfirst == versionCode){
            return false;
        }else{
            sql = "update `appglobal` set `isfirst` = " + versionCode + " where id = 1";
            DBHelper.execute(sql);
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.deviceToken = Utils.getDeviceToken();
        mainView = (WebView) findViewById(R.id.MainView);

        XGPushManager.registerPush(getApplicationContext(), new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                Log.e("TPush", "注册成功，设备token为：" + data);
                //Utils.getToken(myContext, true, data.toString());
                Utils.saveDeviceToken(Utils.deviceToken);
                Map<String, String> params = new HashMap<String, String>();
                params.put("deviceToken", data.toString());
                params.put("userId", Utils.deviceToken);
                Utils.registerDevicetoken(params);
                /*
                if (!hasLoaded) {
                    Message msg = Message.obtain();
                    msg.what = 1002;
                    msg.obj = new String[]{"", ""};
                    myHandler.sendMessage(msg);
                }
                */
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.e("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
                //Utils.getToken(myContext, false, msg);
            }
        });


        if(isFirst()) {
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
        }

        ErrorPanel = (LinearLayout) findViewById(R.id.ErrorPanel);
        ReloadBt = (Button) findViewById(R.id.ReloadBt);
        ReloadBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mainView.setVisibility(View.VISIBLE);
                mainView.loadUrl(loadURL);
                ErrorPanel.setVisibility(View.GONE);
            }
        });
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);

        ((MyApplication)this.getApplicationContext()).setHandler(myHandler);
        myContext = this;
        pd = new MyLoading(this);//ProgressDialog.show(this, "", "connecting ...", true, true);
        pd.setCanceledOnTouchOutside(false);
        pd.setContent("正在加载...");
        pd.show();

        api.registerApp(Constants.APP_ID);

        //Log.e("deviceToken", Utils.deviceToken);

        if(Utils.deviceToken != null && !Utils.deviceToken.equalsIgnoreCase("")) {
            Message msg = Message.obtain();
            msg.what = 1002;
            msg.obj = new String[]{"", ""};
            myHandler.sendMessage(msg);
            hasLoaded = true;
        }


        upgrade();

    }


    private void upgrade(){
        versionCode = getVersionCode(this);

        new Thread() {
            public void run() {
                try {
                    String uri = Constants.upgradeURL;
                    HttpClient client = new DefaultHttpClient();
                    HttpGet get = new HttpGet(uri);
                    HttpResponse res = client.execute(get);
                    String body = EntityUtils.toString(res.getEntity());
                    JSONObject obj = new JSONObject(body);
                    int code = obj.getInt("code");

                    if(code > versionCode){
                        Message msg = Message.obtain();
                        msg.what = 1004;
                        msg.obj = body;
                        myHandler.sendMessage(msg);
                    }
                }catch(Exception e){
                    Log.e("6renyou", e.getMessage());
                }
            }
        }.start();

    }

    public boolean onKeyDown(int keyCode, KeyEvent e){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(mainView != null && mainView.canGoBack() && mainView.getVisibility() == View.VISIBLE && !mainView.getUrl().equals(Constants.homePage)){
                mainView.goBack();
                return true;
            }else{
                /*
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
                */
            }
            //return true;
        }

        return super.onKeyDown(keyCode, e);
    }

    public void onPause(){
        Utils.online = "off";
        Utils.calljs(mainView, "stop");
        super.onPause();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onNewIntent(Intent intent){
        String tmp = intent.getStringExtra("loadurl");
        if(tmp !=null && !tmp.equals("")){
            if(tmp.equalsIgnoreCase(loadURL))return;
            loadURL = tmp;
        }else{
            if(loadURL.equalsIgnoreCase(Constants.homePage))return;
            loadURL = Constants.homePage;
        }
        mainView.setVisibility(View.INVISIBLE);
        mainView.loadUrl(loadURL);
        pd.show();
    }

    public void onStart(){
        Utils.online = "on";
        Utils.calljs(mainView, "start");
        super.onStart();
    }

    public void onStop(){
        Utils.online = "off";
        Utils.calljs(mainView, "stop");
        super.onStop();
    }

    public void onResume(){
        Utils.online = "on";
        Utils.calljs(mainView, "start");
        super.onResume();
        MobclickAgent.onResume(this);
    }


    private int getVersionCode(Context context) {
        int _versionCode = 1;
        try {
            _versionCode = context.getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("6renyou", e.getMessage());
        } finally {
            return _versionCode;
        }
    }

    private int versionCode;

    public void UpdateApk(final String url) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        File sdCard = Environment.getExternalStorageDirectory();
                        String sdPath = sdCard.getAbsolutePath();
                        URL _url = new URL(url);

                        HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
                        conn.connect();
                        int length = conn.getContentLength();
                        InputStream is = conn.getInputStream();

                        File apkFile = new File(sdPath + "/" + "6renyou.apk");
                        FileOutputStream fos = new FileOutputStream(apkFile);
                        int count = 0;

                        byte buf[] = new byte[1024];
                        do {
                            int _num = is.read(buf);
                            count += _num;

                            if (_num <= 0) {
                                break;
                            }
                            fos.write(buf, 0, _num);
                        } while (true);
                        fos.close();
                        is.close();

                        Intent _intent = new Intent(Intent.ACTION_VIEW);
                        _intent.setDataAndType(
                                Uri.parse("file://" + apkFile.toString()),
                                "application/vnd.android.package-archive");
                        MainActivity.this.startActivity(_intent);
                        MainActivity.this.finish();
                    }
                } catch (MalformedURLException e) {
                    Log.e("6renyou", e.getMessage() + " , " + e.toString());
                } catch (IOException e) {
                    Log.e("6renyou", e.getMessage() + " , " + e.toString());
                }
            }
        }).start();
    }


}
