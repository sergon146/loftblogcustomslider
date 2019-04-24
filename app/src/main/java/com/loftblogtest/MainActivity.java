package com.loftblogtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.loftblogtest.slider.RoundSlider;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RoundSlider slider = findViewById(R.id.slider);
        slider.setCurrentValue(50);
        slider.setMinValue(-10);
        slider.setMaxValue(80);
    }
}
