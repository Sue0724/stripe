package com.example.myapplication.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.myapplication.R;

public class StripeView extends View implements SensorEventListener {

    private Context mContext;

    private Resources mResources;
    //Resource类封装了各种应用资源访问的接口，如获取drawable/字符串等等
    //例如：Drawable stripeDrawable = mResources.getDrawable(R.drawable.stripe);

    private Drawable mStripeDrawable;

    private int offset ;

    public final int maxOffset = 20;
    public final int minOffset = 15;

    private final static float GRAVITATIONAL_ACCELERATION = 9.81F;

    private SensorManager mSensorManager;

    private Sensor mAccelerometerSensor;

    private float mAccelerationXPercentage;

    public StripeView(Context context) {
        super(context);

        // 初始化stripeDrawable
        mStripeDrawable = ContextCompat.getDrawable(context, R.drawable.stripe);
    }


    //根据view大小设置drawble大小
    /*@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 设置drawable大小
        mStripeDrawable.setBounds(0, 0, w, h);
    }*/

    public void setOffset(int offset) {
        // 限制offset范围
        this.offset = Math.min(maxOffset, Math.max(minOffset,offset));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);//完成父类绘制

        canvas.drawColor(Color.WHITE);//绘制背景为白色
        //mStripeDrawable.setBounds(0,0,getWidth(),getHeight());//获取view的宽高作为drawable的绘制范围
        //mStripeDrawable.draw(canvas);//调用drawble的draw()方法绘制到canvas
        // 绘制偏移过的stripe
        canvas.translate(offset, 0);
        mStripeDrawable.draw(canvas);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float accelerationX = event.values[SensorManager.DATA_X];
        mAccelerationXPercentage = accelerationX / GRAVITATIONAL_ACCELERATION;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(float x) {
    }
}
