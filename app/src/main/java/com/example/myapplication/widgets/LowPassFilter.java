package com.example.myapplication.widgets;

public class LowPassFilter {

    private float mLastValue;
    private float mFilteredValue;
    private float mTau;

    public LowPassFilter() {
        mTau = 0.5f; // 默认时间常数
    }

    public float filter(float value) {
        mFilteredValue = mTau * mFilteredValue + (1 - mTau) * value;
        return mFilteredValue;
    }

}
