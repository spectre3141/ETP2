package com.zhaw.schoejo2.etp_moodlight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.widget.EditText;
import android.widget.SeekBar;

public class LedActivity extends AppCompatActivity {

    EditText colorByValue;
    SeekBar redSeekbar;
    SeekBar greenSeekbar;
    SeekBar blueSeekbar;
    SeekBar whiteSeekbar;

    int colorValue = 0;
    int redValue = 0;
    int greenValue = 0;
    int blueValue = 0;
    int whiteValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);

        colorByValue = (EditText) findViewById(R.id.colorValueEdit);
        redSeekbar = (SeekBar) findViewById(R.id.redSeekbar);
        greenSeekbar = (SeekBar) findViewById(R.id.greenSeekbar);
        blueSeekbar = (SeekBar) findViewById(R.id.blueSeekbar);
        whiteSeekbar = (SeekBar) findViewById(R.id.whiteSeekbar);

        redSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                redValue = progress;
                colorByValue.setText(calcColor());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
        });

        greenSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                greenValue = progress;
                colorByValue.setText(calcColor());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
        });

        blueSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                blueValue = progress;
                colorByValue.setText(calcColor());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
        });

        whiteSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                whiteValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
        });

    }

    private String calcColor(){
        int buffer = blueValue;
        buffer += (greenValue << 8);
        buffer += (redValue << 16);
        return Integer.toHexString(buffer);
    }

}
