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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
                            }
                            super.onProgressChanged(view, newProgress);
                        }
                    });

                    mainView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            return true;
                        }
                    });
                    mainView.loadUrl("http://m.6renyou.com/android/index");
                    //jsm.sharePicToCircle1(R.drawable.ic_launcher);
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        versionCode = getVersionCode(this);

        new Handler().postDelayed(new Thread() {
            @Override
            public void run() {
                UpdateTask task = new UpdateTask();
                task.execute();
            }
        }, 1000);
*/

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

/*
    private int getVersionCode(Context context) {
        int _versionCode = 0;
        try {
            _versionCode = context.getPackageManager().getPackageInfo("com.liurenyou.im", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return _versionCode;
    }

    @SuppressWarnings("UnnecessarySemicolon")
    private class UpdateTask extends AsyncTask<Integer, Void, String> {
        UpdateTask() {};

        protected String doInBackground(Integer... _array) {
            String result = bench.getVersionCode();
            return result;
        }

        protected void onPostExecute(String result) {
            if (result.length() == 0) {
                Toast.makeText(MainActivity.this, R.string.network_fail, Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject _result = new JSONObject(result);
                    String _apiCode = _result.getString("apiCode");
                    if (_apiCode.equals("10000")) {
                        JSONObject _data = _result.getJSONObject("data");
                        if (_data.getInt("versionCode") > versionCode) {
                            final String _update_url = _data.getString("apkUrl");
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle(getString(R.string.logo_str1))
                                    .setMessage(getString(R.string.logo_str2))
                                    .setPositiveButton(getString(R.string.logo_str3),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    MainActivity.this.UpdateApk(_update_url);
                                                }
                                            })
                                    .setNegativeButton(
                                            getString(R.string.logo_str4),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    dialog.dismiss();
                                                    MainActivity.this
                                                            .startActivity(new Intent(
                                                                    MainActivity.this,
                                                                    MainActivity.class));
                                                    MainActivity.this.finish();
                                                }
                                            }).show();

                        } else {
                            java.lang.Thread.sleep(3000);
                            MainActivity.this.startActivity(new Intent(
                                    MainActivity.this, MainActivity.class));
                            MainActivity.this.finish();
                        }
                    } else {
                        // do nothing
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int versionCode;
    private Dialog dialog;
    private ProgressBar progress;

    public void UpdateApk(final String url) {
        AlertDialog.Builder _builder = new AlertDialog.Builder(this);
        _builder.setTitle(getString(R.string.logo_str5));
        final LayoutInflater _inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View _view = _inflater.inflate(R.layout.activity_logo_update_progress, null);
        progress = (ProgressBar) _view.findViewById(R.id.update_progress);
        _builder.setView(_view);
        dialog = _builder.create();
        dialog.show();

        new Thread(new Runnable() {
            public void run() {
                try {
                    if (Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        int _progress = 0;
                        File sdCard = Environment.getExternalStorageDirectory();
                        String sdPath = sdCard.getAbsolutePath() + "/"
                                + Constants.baseDir;
                        URL _url = new URL(url);

                        HttpURLConnection conn = (HttpURLConnection) _url
                                .openConnection();
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

                            _progress = (int) (((float) count / length) * 100);
                            progress.setProgress(_progress);
                            if (_num <= 0) {
                                break;
                            }
                            fos.write(buf, 0, _num);
                        } while (true);
                        fos.close();
                        is.close();
                        dialog.dismiss();
                        Intent _intent = new Intent(Intent.ACTION_VIEW);
                        _intent.setDataAndType(
                                Uri.parse("file://" + apkFile.toString()),
                                "application/vnd.android.package-archive");
                        MainActivity.this.startActivity(_intent);
                        MainActivity.this.finish();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
*/

}
