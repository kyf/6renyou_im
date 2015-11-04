package com.liurenyou.im.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.liurenyou.im.R;

/**
 * Created by keyf on 2015/11/4.
 */
public class SignalView extends View {

    private Context mContext;

    private void init(Context context){
        mContext = context;
    }

    public SignalView(Context context){
        super(context);
        init(context);
    }

    public SignalView(Context context, AttributeSet attr){
        super(context, attr);
        init(context);
    }

    public SignalView(Context context, AttributeSet attr, int defStyle){
        super(context, attr, defStyle);
        init(context);
    }

    @Override
    public void onDraw(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(mContext.getResources().getColor(R.color.appbase));
        paint.setStrokeWidth(10);
        getMeasuredHeight();
        int lineNumber = 6;
        float step = getMeasuredWidth() / lineNumber;
        float height = getMeasuredHeight();

        for(int i = 1; i <= lineNumber; i++){
            canvas.drawLine(i * step, height - i * step, i * step, height, paint);
        }
    }
}
