package com.liurenyou.im;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.ebudiu.budiu.sdk.BTSScanAPI;

public class BindTravelCardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLayout = R.layout.activity_bind_travel_card;
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String mac_addr = intent.getStringExtra("mac_addr");
        BTSScanAPI.bindDevice(mac_addr);


        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent intent1 = PendingIntent.getActivity(this, 0, new Intent(), 0);
        Notification notify1 = new Notification.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle("6人游提示您")
                .setContentText("您的行李牌绑定成功")
                .setTicker("您的行李牌绑定成功")
                .setContentIntent(intent1)
                .getNotification();
        nm.notify(1, notify1);
    }

}
