package com.liurenyou.im;

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
    }

}
