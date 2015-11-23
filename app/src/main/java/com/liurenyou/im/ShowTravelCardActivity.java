package com.liurenyou.im;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ShowTravelCardActivity extends BaseActivity implements View.OnClickListener {

    public static int RESULT_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLayout = R.layout.activity_show_travel_card;
        super.onCreate(savedInstanceState);

        initView();
    }

    private void initView(){
        ImageView bt = (ImageView) findViewById(R.id.addtravelcardbt);
        Button buybt = (Button) findViewById(R.id.buybt);
        bt.setOnClickListener(this);
        buybt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.addtravelcardbt:{
                Intent intent = new Intent(this, ScanTravelCardActivity.class);
                startActivityForResult(intent, RESULT_CODE);
                break;
            }
            case R.id.buybt:{
                Toast.makeText(this, "暂未开放购买", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        this.finish();
    }
}

