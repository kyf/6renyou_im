package com.liurenyou.im;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Bundle;

import com.liurenyou.im.db.TravelDB;
import com.umeng.analytics.MobclickAgent;

public class ShowActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent;
                String macAddr = hasTravelCard();
                if(!macAddr.equals("")){
                    intent = new Intent(ShowActivity.this, BindTravelCardActivity.class);
                    intent.putExtra("mac_addr", macAddr);
                    intent.putExtra("action", "rebind");
                }else{
                    intent = new Intent(ShowActivity.this, ShowTravelCardActivity.class);//MainActivity.class);
                }

                ShowActivity.this.startActivity(intent);
                ShowActivity.this.finish();
            }
        }, 1000);
    }


    private String hasTravelCard(){
        String result = "";
        String sql = "select `mac_addr` from `travel_card` limit 1";
        Cursor cursor = TravelDB.query(sql);
        if(cursor.getCount() == 0)return result;
        cursor.moveToFirst();
        result = cursor.getString(cursor.getColumnIndex("mac_addr"));
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
