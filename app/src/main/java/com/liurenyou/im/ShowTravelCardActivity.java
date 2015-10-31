package com.liurenyou.im;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ShowTravelCardActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLayout = R.layout.activity_show_travel_card;
        super.onCreate(savedInstanceState);

        initView();
    }

    private void initView(){
        Button bt = (Button) findViewById(R.id.addtravelcardbt);
        bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.addtravelcardbt:{
                Intent intent = new Intent(this, ScanTravelCardActivity.class);
                startActivity(intent);
            }
        }
    }
}

