package com.zhaw.schoejo2.etp_moodlight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LedActivity extends AppCompatActivity {

    public int colorValue = getColor(R.color.colorByValue);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);
    }
}
