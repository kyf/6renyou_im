package com.liurenyou.im;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ebudiu.budiu.sdk.BTSScanAPI;
import com.ebudiu.budiu.sdk.OnDeviceListener;
import com.ebudiu.budiu.sdk.SDKAPI;
import com.ebudiu.budiu.sdk.ScanCtrListener;
import com.kyleduo.switchbutton.SwitchButton;
import com.liurenyou.im.db.TravelDB;
import com.liurenyou.im.widget.AlertDialog;
import com.liurenyou.im.widget.MyLoading;
import com.liurenyou.im.widget.SignalView;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BindTravelCardActivity extends BaseActivity implements View.OnClickListener, TencentLocationListener {

    private TextView DeviceTipLabel;

    private MyLoading myLoading;

    private String macAddr = "";

    private SignalView signalView;

    private double[] langAndLat = null;

    private boolean connectState = false;

    private String travelCardMac = "";

    private int is_connect = 1;

    private int is_disconnect = 1;

    private double lastDistance = 0;

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            //Log.e("6renyou", msg.what + "");
            switch(msg.what){
                case 1001:{
                    /*
                    myLoading.dismiss();
                    signalView.setVisibility(View.VISIBLE);
                    if(connectState) {
                        updateResult(getResources().getString(R.string.notify_connected_travelcard));
                    }
                    connectState = false;
                    */
                    break;
                }
                case 1002:{
                    double distance = (double) msg.obj;
                    String strdistance = String.valueOf(distance);

                    if(distance < 0){
                        if(lastDistance > 0){
                            lastDistance = distance;
                            return;
                        }
                        if(connectState) {
                            updateResult(getResources().getString(R.string.notify_disconnect_travelcard));
                        }
                        connectState = false;
                        signalView.setVisibility(View.INVISIBLE);
                        DeviceTipLabel.setText(getResources().getString(R.string.notify_disconnect_travelcard));
                        SDKAPI.startScanDevice();
                        return;
                    }

                    double[] locations = getLocation();
                    if(locations != null){
                        langAndLat = locations;
                        saveLocation();
                    }

                    connectState = true;
                    DeviceTipLabel.setText(getResources().getString(R.string.notify_connected_travelcard));
                    myLoading.dismiss();
                    signalView.setVisibility(View.VISIBLE);

                    break;
                }
                case 1003:{
                    myLoading.dismiss();
                    signalView.setVisibility(View.VISIBLE);

                    if(!connectState) {
                        updateResult(getResources().getString(R.string.notify_connected_travelcard));
                    }
                    connectState = true;
                    break;
                }
                case 1004:{
                    DeviceTipLabel.setText(getResources().getString(R.string.notify_disconnect_travelcard));
                    if(connectState) {
                        updateResult(getResources().getString(R.string.notify_disconnect_travelcard));
                    }
                    connectState = false;
                    SDKAPI.startScanDevice();
                    break;
                }
                case 1005:{
                    connectState = (boolean) msg.obj;
                    if(!connectState){
                        SDKAPI.startScanDevice();
                    }
                    break;
                }
                case 4001:{
                    myLoading.dismiss();
                    DeviceTipLabel.setText(getResources().getString(R.string.notify_disconnect_travelcard));
                    signalView.setVisibility(View.INVISIBLE);
                    updateResult(getResources().getString(R.string.notify_disconnect_travelcard));
                    break;
                }
                default:{
                    myLoading.dismiss();
                    SDKAPI.startScanDevice();
                    DeviceTipLabel.setText("");
                    signalView.setVisibility(View.INVISIBLE);
                }
            }
        }

    };


    private void timeoutScan(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    int second = 1000;
                    Thread.sleep(second * 5);
                    if(!connectState) {
                        myHandler.sendEmptyMessage(4001);
                    }
                }catch(InterruptedException e){

                }
            }
        }).start();
    }

    private void regTencentLocation(){
        TencentLocationRequest request = TencentLocationRequest.create();
        TencentLocationManager locationManager = TencentLocationManager.getInstance(this);
        locationManager.requestLocationUpdates(request, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLayout = R.layout.activity_bind_travel_card;
        super.onCreate(savedInstanceState);

        regTencentLocation();

        Intent intent = getIntent();
        macAddr = intent.getStringExtra("mac_addr");

        String action = intent.getStringExtra("action");
        initView();

        //Log.e("6renyou", "special mac:" + macAddr);
        CardListener instance = CardListener.getInstance();
        instance.setMyHandler(myHandler);
        instance.autoConnect = true;
        instance.macAddr = macAddr;


        SDKAPI.setDeviceListener(instance);
        instance.isListen = true;
        SDKAPI.startScanDevice();

        timeoutScan();

        DeviceTipLabel.setText("");
        signalView.setVisibility(View.INVISIBLE);
        boolean requestConnectState = SDKAPI.getConnectedDevices();


        if(macAddr != null && !macAddr.equals("")) {

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

                double[] locations = getLocation();
                if(locations != null){
                    langAndLat = locations;
                    saveLocation();
                }
            }else{
                if(!SDKAPI.isBluetoothOn(this)){
                    Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(mIntent, 1);
                    return;
                }
                myLoading.show();
                if(instance.isConnected){
                    Message msg = Message.obtain();
                    msg.what = 1002;
                    msg.obj = (double) 0;
                    myHandler.sendMessage(msg);
                }
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

    private void getTravelCard(){
        String sql = "select `mac_addr`, `is_disconnect`, `is_connect` from `travel_card` limit 1";
        Cursor cursor = TravelDB.query(sql);
        if(cursor.getCount() == 0)return;
        cursor.moveToFirst();
        travelCardMac = cursor.getString(cursor.getColumnIndex("mac_addr"));
        is_disconnect = cursor.getInt(cursor.getColumnIndex("is_disconnect"));
        is_connect = cursor.getInt(cursor.getColumnIndex("is_connect"));
    }

    public void initView(){
        ImageView gobackbt = (ImageView) findViewById(R.id.gobackbt);
        gobackbt.setOnClickListener(this);
        DeviceTipLabel = (TextView) findViewById(R.id.DeviceTipLabel);
        signalView = (SignalView) findViewById(R.id.signalView);
        myLoading = new MyLoading(this);
        myLoading.setContent("正在更新数据...");
        ImageView sosbt = (ImageView) findViewById(R.id.sosbt);
        sosbt.setOnClickListener(this);
        getTravelCard();

        DeviceTipLabel.setOnClickListener(this);
        SwitchButton SbDisconnect = (SwitchButton) findViewById(R.id.SwitchBtDisconnect);
        SwitchButton SbConnect = (SwitchButton) findViewById(R.id.SwitchBtConnect);

        if(is_disconnect == 0){
            SbDisconnect.setChecked(false);
        }

        if(is_connect == 0){
            SbConnect.setChecked(false);
        }

        SbDisconnect.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton var1, boolean var2){
                if(var2){
                    is_disconnect = 1;
                }else{
                    is_disconnect = 0;
                }
                String sql = "update `travel_card` set `is_disconnect` = " + is_disconnect;
                TravelDB.execute(sql);

            }
        });

        SbConnect.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton var1, boolean var2){
                if(var2){
                    is_connect = 1;
                }else{
                    is_connect = 0;
                }
                String sql = "update `travel_card` set `is_connect` =  " + is_connect;
                TravelDB.execute(sql);
            }
        });

    }

    private void saveLocation(){
        double longitude = langAndLat[0];
        double latitude = langAndLat[1];
        String sql = "update `travel_card` set `longitude` =  '" + longitude + "', `latitude` = '"+latitude+"'";
        TravelDB.execute(sql);
    }

    private double[] getLocationByDb(){
        double longitude = 0;
        double latitude = 0;
        String sql = "select `longitude`, `latitude` from `travel_card` limit 1";
        Cursor cursor = TravelDB.query(sql);
        if(cursor.getCount() == 0)return null;
        cursor.moveToFirst();

        longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
        latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
        return new double[]{longitude, latitude};
    }

    public boolean onKeyDown(int keyCode, KeyEvent e){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(this, MainActivity.class);
            String url = "";
            intent.putExtra("loadurl", url);
            startActivity(intent);
            finish();
           return true;
        }

        return super.onKeyDown(keyCode, e);
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.gobackbt:{
                Intent intent = new Intent(this, MainActivity.class);
                String url = "";
                intent.putExtra("loadurl", url);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.sosbt:{
                double[] pos = getLocationByDb();
                if(pos == null){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.tip_no_lang_lat), Toast.LENGTH_SHORT).show();
                }else {
                    showMap(pos);
                }
                break;
            }
        }
    }


    public void showMap(double[] locations){
        Intent intent = new Intent(this, MainActivity.class);
        String url = "http://m.6renyou.com/android/latlng?lng=" + locations[0] + "&lat=" + locations[1];
        intent.putExtra("loadurl", url);
        startActivity(intent);
    }


    public void updateResult(String str){
        if(is_disconnect == 0 && connectState)return;
        if(is_connect == 0 && !connectState)return;

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, BindTravelCardActivity.class);

        PendingIntent intent1 = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notify1 = new Notification.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(getResources().getString(R.string.notify_title))
                .setContentText(str)
                .setTicker(str)
                .setContentIntent(intent1)
                .getNotification();
        notify1.defaults |= Notification.DEFAULT_SOUND;
        notify1.defaults |= Notification.DEFAULT_VIBRATE;
        nm.notify(1, notify1);
    }

    public double[] getLocation(){
        return langAndLat;
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        langAndLat = new double[]{tencentLocation.getLongitude(), tencentLocation.getLatitude()};
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }
}
