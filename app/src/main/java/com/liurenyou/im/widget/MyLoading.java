package com.liurenyou.im.widget;

/**
 * Created by keyf on 2015/9/29.
 */

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liurenyou.im.R;

/**
 * Created by keyf on 2015/9/29.
 */
public class MyLoading extends Dialog {
    private TextView tv;

    private String content = "";

    public MyLoading(Context context) {
        super(context, R.style.loadingDialogStyle);
    }

    private MyLoading(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        tv = (TextView) this.findViewById(R.id.tv);
        tv.setText(content);
        if(content.equals("")){
            tv.setVisibility(View.GONE);
        }
        LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.LinearLayout);
        linearLayout.getBackground().setAlpha(210);
    }

    public void setContent(String content) {
        this.content = content;
    }
}