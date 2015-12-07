package com.liurenyou.im;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ShowPositionActivity extends BaseActivity {

    private WebView mapView;

    private LinearLayout ErrorPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLayout = R.layout.activity_show_position;
        super.onCreate(savedInstanceState);

        ErrorPanel = (LinearLayout) findViewById(R.id.MapErrorPanel);

        Intent intent = getIntent();
        double lang = intent.getDoubleExtra("long", 0);
        double lat = intent.getDoubleExtra("lat", 0);

        mapView = (WebView) findViewById(R.id.MapView);

        WebSettings settings = mapView.getSettings();

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
        JavaScriptMethods jsm = new JavaScriptMethods(this);
        mapView.addJavascriptInterface(jsm, "JavaScriptMethods");

        mapView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {

                }
                super.onProgressChanged(view, newProgress);
            }
        });

        mapView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
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


        mapView.loadUrl("http://m.6renyou.com/game/gmap?lng=" + lang  + "&lat=" + lat);
    }

}
