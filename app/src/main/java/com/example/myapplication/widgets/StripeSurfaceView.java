package com.example.myapplication.widgets;

import android.annotation.SuppressLint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.example.myapplication.R;

import java.util.concurrent.TimeUnit;


import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;


public class StripeSurfaceView extends SurfaceView implements SurfaceHolder.Callback , SensorEventListener{

    private final Object mLock = new Object();

    Subscription mLooper = Subscriptions.empty();
    private HandlerThread mCalculatePositionThread;
    private Handler mCalculateHandler;

    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private float mAccelerationXPercentage;

    private float SENSITIVITY = 15.0F;
    private final static int MSG_CALCULATE = 233;
    private int mWidth;

    private int mHeight;
    private SurfaceHolder mSurfaceHolder;

    //private SurfaceHolder mSurfaceHolder;

    private float mStripeOffsetX = 0;

    public float x,y;

    private final static long INVALID_TIME = -1;

    private long mLastTimeMillis = INVALID_TIME;

    public StripeSurfaceView(Context context) {
        super(context);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }


    public StripeSurfaceView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

    public StripeSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            initView(attrs);
        }


    @Override
//编译器可以给你验证@Override下面的方法名是否是你父类中所有的，如果没有则报错。例如，你如果没写@Override，而你下面的方法名又写错了，这时你的编译器是可以编译通过的，因为编译器以为这个方法是你的子类中自己增加的方法。
    public void surfaceCreated(SurfaceHolder holder){

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        //createSnowFlakes();
        drawStripes();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        stopFall();
    }

    @SuppressLint("ResourceAsColor")
    private void drawStripes() {
        // calculate width and interval
        Canvas canvas = null;
        try {
            canvas = mSurfaceHolder.lockCanvas();
            if (canvas != null) {

                canvas.drawColor(Color.BLACK);
                float interval = canvas.getWidth() / 8;

              // draw stripes
                Paint paint = new Paint();

                paint.setColor(Color.CYAN);
                paint.setAlpha(128);
                paint.setStrokeWidth(50);
                paint.setStyle(Paint.Style.FILL);
                paint.setAntiAlias(true);

                for (int i = 0; i < canvas.getWidth(); i ++) {
                    x = i * interval + mStripeOffsetX;
                    canvas.drawLine(x, 0, x, canvas.getHeight(), paint);
                }
                mCalculateHandler.sendEmptyMessage(MSG_CALCULATE);
            }
        } finally {
            if (canvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float accelerationX = event.values[0];
        mStripeOffsetX = accelerationX * SENSITIVITY;
        invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    private void notifyCalculateThreadStop() {
        mCalculateHandler.removeMessages(MSG_CALCULATE);
    }

    private void startLooper() {
        mLooper.unsubscribe();
        mLooper = Observable.interval(1, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    public void call(Long aLong) {
                        drawStripes();
                    }
                });
    }

    private void stopLooper() {
        mLooper.unsubscribe();
    }

    /**
     * 开始下雪动画
     */
    public void startFall() {
        setVisibility(VISIBLE);
        mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        startLooper();
    }

    /**
     * 停止下雪动画
     */
    public void stopFall() {
        setVisibility(GONE);
        mSensorManager.unregisterListener(this);
        notifyCalculateThreadStop();
        stopLooper();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopFall();
        mCalculatePositionThread.quit();
    }


  /*  private void initCalculateHandler() {

        mCalculateHandler = new Handler(mCalculatePositionThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                long currentTimeMillis = System.currentTimeMillis();
                // 获取锁定对象锁,保证线程安全
                synchronized (mLock) {
                    Canvas canvas = mSurfaceHolder.lockCanvas();
                    if (canvas != null) {
                        // 绘制条纹
                        drawStripes();
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                // 通过发送空消息来触发下一次绘制
                mCalculateHandler.sendEmptyMessage(0);
            }
        };
    }*/


    private void initCalculateHandler() {

        mCalculateHandler = new Handler(mCalculatePositionThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                long currentTimeMillis = System.currentTimeMillis();

                if (mLastTimeMillis != INVALID_TIME) {

                    float deltaTime = (currentTimeMillis - mLastTimeMillis) / 1000.0F;

                    x += mStripeOffsetX;

                }

                mLastTimeMillis = currentTimeMillis;
            }
        };
    }

    private void initView(AttributeSet attrs) {

        /*TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.StripeSurfaceView);
        applyAttrsFromXML(array);
        array.recycle();*/

        initSensorManager();
        initCalculateThread();
        initCalculateHandler();

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    /*private void applyAttrsFromXML(TypedArray array) {
        mSnowFlakeBitmap = BitmapFactory.decodeResource(getResources(),
                array.getResourceId(R.styleable.StripeSurfaceView_src, -1));
        mSnowFlakeBitmapPivotX = mSnowFlakeBitmap.getWidth() / 2.0F;
        mSnowFlakeBitmapPivotY = mSnowFlakeBitmap.getHeight() / 2.0F;
    }*/

    private void initSensorManager() {

        if (isInEditMode()) {
            return;
        }

        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void initCalculateThread() {
        mCalculatePositionThread = new HandlerThread("calculate_thread");
        mCalculatePositionThread.start();
    }


}
