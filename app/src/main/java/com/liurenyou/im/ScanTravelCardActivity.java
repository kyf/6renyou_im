package com.liurenyou.im;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ebudiu.budiu.sdk.BTSScanAPI;
import com.ebudiu.budiu.sdk.SDKAPI;
import com.ebudiu.budiu.sdk.ScanCtrListener;
import com.liurenyou.im.widget.MyLoading;

public class ScanTravelCardActivity extends BaseActivity implements ScanCtrListener {

    private static final String LogTag = "ScanTravelCardActivity";

    private MyLoading myLoading;

    private String macAddr = "";

    private TextView showLabel;

    private Button reScanBt, confirmBindBt;

    private static final int SCAN_INTERVAL = 5;

    private View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            switch(view.getId()){
                case R.id.rescanbt:{
                    if(!SDKAPI.isBluetoothOn(ScanTravelCardActivity.this)){
                        Toast.makeText(ScanTravelCardActivity.this, "请先开启蓝牙", Toast.LENGTH_SHORT).show();
                        reScanBt.setVisibility(View.VISIBLE);
                        return;
                    }
                    if(!TextUtils.isEmpty(macAddr)) {
                        SDKAPI.disconnectDevice(macAddr);
                    }
                    myLoading.show();
                    BTSScanAPI.scanStart(SCAN_INTERVAL);
                    break;
                }
                case R.id.confirmbindbt:{
                    Intent intent = new Intent(ScanTravelCardActivity.this, BindTravelCardActivity.class);
                    intent.putExtra("mac_addr", macAddr);
                    startActivity(intent);
                    break;
                }
            }
        }
    };

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case 1001:{
                    showLabel.setText("找到设备:" + macAddr);
                    myLoading.dismiss();
                    reScanBt.setVisibility(View.VISIBLE);
                    confirmBindBt.setVisibility(View.VISIBLE);
                    BTSScanAPI.doConnectDevice(macAddr);
                    break;
                }
                default:{
                    myLoading.dismiss();
                    reScanBt.setVisibility(View.VISIBLE);
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLayout = R.layout.activity_scan_travel_card;
        super.onCreate(savedInstanceState);

        initView();
    }

    private void initView(){
        showLabel = (TextView) findViewById(R.id.show_label);
        reScanBt = (Button) findViewById(R.id.rescanbt);
        confirmBindBt = (Button) findViewById(R.id.confirmbindbt);

        reScanBt.setOnClickListener(clickListener);
        confirmBindBt.setOnClickListener(clickListener);

        myLoading = new MyLoading(this);
        myLoading.setContent("正在扫描设备...");
        myLoading.setCanceledOnTouchOutside(false);

        BTSScanAPI.setScanListener(this);

        if(!SDKAPI.isBluetoothLeSupported(this)){
            Toast.makeText(this, "您的设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!SDKAPI.isBluetoothOn(this)){
            Toast.makeText(this, "请先开启蓝牙", Toast.LENGTH_SHORT).show();
            reScanBt.setVisibility(View.VISIBLE);
            return;
        }

        myLoading.show();
        BTSScanAPI.scanStart(SCAN_INTERVAL);
    }


    @Override
    public void scanFail() {
        myHandler.sendEmptyMessage(1002);
    }

    @Override
    public void scanSuccess(final String s) {
        macAddr = s;
        myHandler.sendEmptyMessage(1001);
    }

    @Override
    public void connectSucess() {

    }

    @Override
    public void connectFail() {

    }

    @Override
    public void disConnect() {

    }

    @Override
    public void bindSucess(final String s) {

    }

    @Override
    public void bindFail() {

    }

    @Override
    public void sendStatus(String s) {

    }

    @Override
    public void unBindSucess(final String s) {

    }

    @Override
    public void unBindFail() {

    }

    @Override
    public void onDestroy(){
        if(!TextUtils.isEmpty(macAddr)) {
            SDKAPI.disconnectDevice(macAddr);
        }
        super.onDestroy();
    }

}
