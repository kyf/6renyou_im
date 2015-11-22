package com.liurenyou.im;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

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
                if(false){
                    intent = new Intent(ShowActivity.this, BindTravelCardActivity.class);
                }else{
                    intent = new Intent(ShowActivity.this, ShowTravelCardActivity.class);//MainActivity.class);
                }


                ShowActivity.this.startActivity(intent);
                ShowActivity.this.finish();
            }
        }, 1000);
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
