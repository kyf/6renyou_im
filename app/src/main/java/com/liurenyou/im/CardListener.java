package com.liurenyou.im;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ebudiu.budiu.sdk.OnDeviceListener;
import com.ebudiu.budiu.sdk.ScanCtrListener;
import java.util.ArrayList;

/**
 * Created by keyf on 2015/12/9.
 */
public class CardListener implements ScanCtrListener, OnDeviceListener {

    private static CardListener instance = null;

    private Handler myHandler = null;

    public boolean autoConnect = false;

    public boolean isListen = false;

    public String macAddr = "";

    private CardListener(){
    }

    public static CardListener getInstance(){
        if(instance == null){
            instance = new CardListener();
        }

        return instance;
    }

    public void setMyHandler(Handler myHandler){
        this.myHandler = myHandler;
    }

    @Override
    public void scanFail() {
        myHandler.sendEmptyMessage(2002);
    }

    @Override
    public void scanSuccess(final String s) {

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
    public boolean isAutoConnect(String mac) {
        if(mac.equalsIgnoreCase(macAddr) && autoConnect){
            Log.e("6renyou", "auto connect:" + mac + ", true");
            return true;
        }else{
            Log.e("6renyou", "auto connect:" + mac + ", false");
            return false;
        }
    }

    @Override
    public boolean isBoundDevice(String mac) {
        return true;
    }

    @Override
    public void deviceDiscovery(String mac, double distance) {
        Message msg = Message.obtain();
        msg.what = 1001;
        msg.obj = mac;
        Log.e("6renyou", "device discovery:" + mac);
        myHandler.sendMessage(msg);
    }

    @Override
    public void devicePower(String mac, int power) {

    }

    @Override
    public void connectedDevices(ArrayList list){
        Message msg = Message.obtain();
        msg.what = 1005;
        if(list.size() > 0 && list.get(0).toString().length() > 2) {
            msg.obj = true;
        }else{
            msg.obj = false;
        }
        myHandler.sendMessage(msg);
    }

    @Override
    public void deviceDistance(String mac, double distance) {
        Message msg = Message.obtain();
        msg.what = 1002;
        msg.obj = distance;
        myHandler.sendMessage(msg);
    }

    @Override
    public void deviceConnected(String mac) {
        Log.e("6renyou", "connected : " + mac);
        Message msg = Message.obtain();
        msg.what = 1003;
        msg.obj = mac;
        myHandler.sendMessage(msg);
    }

    @Override
    public void deviceDisconnect(String mac) {
        Message msg = Message.obtain();
        msg.what = 1004;
        msg.obj = mac;
        myHandler.sendMessage(msg);
    }

}
