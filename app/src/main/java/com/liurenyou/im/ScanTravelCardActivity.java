package com.liurenyou.im;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ebudiu.budiu.sdk.BTSScanAPI;
import com.ebudiu.budiu.sdk.OnDeviceListener;
import com.ebudiu.budiu.sdk.SDKAPI;
import com.ebudiu.budiu.sdk.ScanCtrListener;
import com.liurenyou.im.db.TravelDB;
import com.liurenyou.im.widget.MyLoading;

import java.util.ArrayList;

public class ScanTravelCardActivity extends BaseActivity {

    private static final String LogTag = "ScanTravelCardActivity";

    private MyLoading myLoading;

    private String macAddr = "";

    private double distance = 0;

    private TextView showLabel;

    private Button reScanBt, confirmBindBt;

    private ImageView scan_success_image;

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
                    BTSScanAPI.bindDevice(macAddr);
                    if(!TextUtils.isEmpty(macAddr)) {
                        //SDKAPI.disconnectDevice(macAddr);
                    }
                    String sql = "insert into `travel_card`(`mac_addr`, `is_disconnect`, `is_connect`) values('" + macAddr + "', 1, 1)";
                    TravelDB.execute(sql);
                    setResult(ShowTravelCardActivity.RESULT_CODE);
                    Intent intent = new Intent(ScanTravelCardActivity.this, BindTravelCardActivity.class);
                    intent.putExtra("mac_addr", macAddr);
                    intent.putExtra("distance", distance);
                    startActivity(intent);
                    finish();

                    break;
                }
                case R.id.gobackbt:{
                    finish();
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
                    macAddr = (String) msg.obj;
                    showLabel.setText(getResources().getString(R.string.scan_success_text));
                    myLoading.dismiss();
                    reScanBt.setVisibility(View.VISIBLE);
                    confirmBindBt.setVisibility(View.VISIBLE);
                    scan_success_image.setVisibility(View.VISIBLE);
                    BTSScanAPI.doConnectDevice(macAddr);
                    break;
                }
                case 1002:{
                    distance = (double) msg.obj;
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
        scan_success_image = (ImageView) findViewById(R.id.scan_success_image);

        reScanBt.setOnClickListener(clickListener);
        confirmBindBt.setOnClickListener(clickListener);

        myLoading = new MyLoading(this);
        myLoading.setContent("正在扫描设备...");
        myLoading.setCanceledOnTouchOutside(false);
        ImageView gobackbt = (ImageView) findViewById(R.id.gobackbt);
        gobackbt.setOnClickListener(clickListener);


        if(!SDKAPI.isBluetoothLeSupported(this)){
            Toast.makeText(this, "您的设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }

        CardListener instance = CardListener.getInstance();
        instance.setMyHandler(myHandler);
        SDKAPI.setDeviceListener(instance);
        BTSScanAPI.setScanListener(instance);
        instance.isListen = true;
        if(!SDKAPI.isBluetoothOn(this)){
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(mIntent, 1);
            return;
        }

        myLoading.show();
        BTSScanAPI.scanStart(SCAN_INTERVAL);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "蓝牙已经开启", Toast.LENGTH_SHORT).show();
                myLoading.show();
                BTSScanAPI.scanStart(SCAN_INTERVAL);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "不允许蓝牙开启", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }
}
