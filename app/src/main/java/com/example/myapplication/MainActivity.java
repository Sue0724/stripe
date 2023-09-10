package com.example.myapplication;


import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.widgets.StripeSurfaceView;


public class MainActivity extends AppCompatActivity  {


    private StripeSurfaceView mStripeSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch mSwitch = (Switch) findViewById(R.id.begin_snowing);
        mStripeSurfaceView = (StripeSurfaceView) findViewById(R.id.stripesurfaceview);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    mStripeSurfaceView.startFall();
                } else {
                    mStripeSurfaceView.stopFall();
                }
            }
        });
    }

}