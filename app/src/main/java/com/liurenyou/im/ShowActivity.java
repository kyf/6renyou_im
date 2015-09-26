package com.liurenyou.im;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

public class ShowActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(ShowActivity.this, MainActivity.class);
                ShowActivity.this.startActivity(intent);
                ShowActivity.this.finish();
            }
        }, 1000);
    }

}
