package com.liurenyou.im;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class GuideActivity extends Activity implements View.OnClickListener {

    private LinearLayout mContainer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        mContainer = (LinearLayout) findViewById(R.id.container);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(getWinWidth(), getWinHeight());


        ImageView imageView1 = new ImageView(this);
        imageView1.setLayoutParams(params);
        imageView1.setImageResource(R.drawable.guide1);
        imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
        mContainer.addView(imageView1);

        ImageView imageView2 = new ImageView(this);
        imageView2.setLayoutParams(params);
        imageView2.setImageResource(R.drawable.guide2);
        imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView2.setBackgroundColor(Color.RED);
        mContainer.addView(imageView2);

        ImageView imageView3 = new ImageView(this);
        imageView3.setLayoutParams(params);
        imageView3.setImageResource(R.drawable.guide3);
        imageView3.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView3.setBackgroundColor(Color.GRAY);
        mContainer.addView(imageView3);

        imageView3.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private int getWinWidth(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
    private int getWinHeight(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    @Override
    public void onClick(View view){
        this.finish();
    }
}
