package com.liurenyou.im;

import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {

    protected int mLayout = R.layout.activity_base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mLayout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.hide();
    }

}
