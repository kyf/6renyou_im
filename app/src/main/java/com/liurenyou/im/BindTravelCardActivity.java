package com.liurenyou.im;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ebudiu.budiu.sdk.BTSScanAPI;
import com.ebudiu.budiu.sdk.OnDeviceListener;
import com.ebudiu.budiu.sdk.SDKAPI;
import com.ebudiu.budiu.sdk.ScanCtrListener;
import com.liurenyou.im.widget.AlertDialog;
import com.liurenyou.im.widget.MyLoading;
import com.liurenyou.im.widget.SignalView;

public class BindTravelCardActivity extends BaseActivity implements View.OnClickListener, OnDeviceListener {

    private String macAddr = "";

    private double distance = 0;

    private TextView DeviceTipLabel;

    private MyLoading myLoading;

    private SignalView signalView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLayout = R.layout.activity_bind_travel_card;
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        macAddr = intent.getStringExtra("mac_addr");
        String action = intent.getStringExtra("action");
        double distance = intent.getDoubleExtra("distance", 0);
        initView();
        SDKAPI.setDeviceListener(this);
        DeviceTipLabel.setText("");
        signalView.setVisibility(View.INVISIBLE);
        if(!macAddr.equals("")) {

            if(action == null || action.equals("")) {
                NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                PendingIntent intent1 = PendingIntent.getActivity(this, 0, new Intent(), 0);
                Notification notify1 = new Notification.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setContentTitle(getResources().getString(R.string.notify_title))
                        .setContentText(getResources().getString(R.string.notify_bind_success))
                        .setTicker(getResources().getString(R.string.notify_bind_success))
                        .setContentIntent(intent1)
                        .getNotification();
                nm.notify(1, notify1);
                DeviceTipLabel.setText(R.string.notify_bind_success);
                signalView.setVisibility(View.VISIBLE);
            }else{
                if(!SDKAPI.isBluetoothOn(this)){
                    Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(mIntent, 1);
                    return;
                }
                myLoading.show();

                SDKAPI.startScanDevice();
                DeviceTipLabel.setText("");
                signalView.setVisibility(View.INVISIBLE);

            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "蓝牙已经开启", Toast.LENGTH_SHORT).show();
                myLoading.show();

                SDKAPI.startScanDevice();
                DeviceTipLabel.setText("");
                signalView.setVisibility(View.INVISIBLE);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "不允许蓝牙开启", Toast.LENGTH_SHORT).show();
                DeviceTipLabel.setText(R.string.notify_bluetooth_closed);
            }
        }

    }

    public void initView(){
        ImageView gobackbt = (ImageView) findViewById(R.id.gobackbt);
        gobackbt.setOnClickListener(this);
        DeviceTipLabel = (TextView) findViewById(R.id.DeviceTipLabel);
        signalView = (SignalView) findViewById(R.id.signalView);
        myLoading = new MyLoading(this);
        myLoading.setContent("正在更新数据...");
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.gobackbt:{
                finish();
                break;
            }
        }
    }

    @Override
    public boolean isAutoConnect(String mac) {
        return mac.equalsIgnoreCase(macAddr)  ? true : false;
    }

    @Override
    public boolean isBoundDevice(String mac) {
        return true;
    }

    @Override
    public void deviceDiscovery(String mac, double distance) {}

    @Override
    public void devicePower(String mac, int power) {

    }

    @Override
    public void deviceDistance(String mac, double distance) {
        if(!mac.equalsIgnoreCase(macAddr))return;
        this.distance = distance;
        String strdistance = String.valueOf(distance);
        if(distance < 0){
            updateResult(getResources().getString(R.string.notify_disconnect_travelcard));
            signalView.setVisibility(View.INVISIBLE);
            DeviceTipLabel.setText(getResources().getString(R.string.notify_disconnect_travelcard));
            return;
        }
        DeviceTipLabel.setText(getResources().getString(R.string.notify_current_distance) + strdistance);
        myLoading.dismiss();
        signalView.setVisibility(View.VISIBLE);
    }

    @Override
    public void deviceConnected(String mac) {
        myLoading.dismiss();
        signalView.setVisibility(View.VISIBLE);
    }

    @Override
    public void deviceDisconnect(String mac) {
        DeviceTipLabel.setText(getResources().getString(R.string.notify_disconnect_travelcard));
        updateResult(getResources().getString(R.string.notify_disconnect_travelcard));
    }


    public void updateResult(String str){
        final AlertDialog alertDialog = new AlertDialog(this);
        alertDialog.builder().setTitle("提示您")
                            .setMsg(str)
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });
        alertDialog.show();
    }
}
