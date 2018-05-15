package com.zhaw.schoejo2.etp_moodlight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

public class LedActivity extends AppCompatActivity {

    // GUI
    EditText colorByValueEdit;
    Button ledCommitButton;
    SeekBar redSeekbar;
    SeekBar greenSeekbar;
    SeekBar blueSeekbar;
    SeekBar whiteSeekbar;

    Button colorA0Button;
    Button colorA1Button;
    Button colorA2Button;
    Button colorA3Button;
    Button colorA4Button;
    Button colorA5Button;
    Button colorB0Button;
    Button colorB1Button;
    Button colorB2Button;
    Button colorB3Button;
    Button colorB4Button;
    Button colorB5Button;
    Button colorC0Button;
    Button colorC1Button;
    Button colorC2Button;
    Button colorC3Button;
    Button colorC4Button;
    Button colorC5Button;
    Button colorD0Button;
    Button colorD1Button;
    Button colorD2Button;
    Button colorD3Button;
    Button colorD4Button;
    Button colorD5Button;

    // constants
    final private String HEX_FORMAT = "0x";

    // variables
    int colorValue = 0;
    int redValue = 0;
    int greenValue = 0;
    int blueValue = 0;
    int whiteValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);

        // GUI initialization
        colorByValueEdit = (EditText) findViewById(R.id.colorValueEdit);
        ledCommitButton = (Button) findViewById(R.id.ledCommitButton);
        redSeekbar = (SeekBar) findViewById(R.id.redSeekbar);
        greenSeekbar = (SeekBar) findViewById(R.id.greenSeekbar);
        blueSeekbar = (SeekBar) findViewById(R.id.blueSeekbar);
        whiteSeekbar = (SeekBar) findViewById(R.id.whiteSeekbar);

        ledCommitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buffer = colorByValueEdit.getText().toString();
                if (buffer.length() == 6){
                    redValue = Integer.decode(HEX_FORMAT + buffer.substring(0, 2));
                    greenValue = Integer.decode(HEX_FORMAT + buffer.substring(2, 4));
                    blueValue = Integer.decode(HEX_FORMAT + buffer.substring(4, 6));
                    redSeekbar.setProgress(redValue);
                    greenSeekbar.setProgress(greenValue);
                    blueSeekbar.setProgress(blueValue);
                } else {
                    Toast.makeText(getApplicationContext(), "invalid input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        redSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                redValue = progress;
                colorByValueEdit.setText(calcColor());
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
                colorByValueEdit.setText(calcColor());
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
                colorByValueEdit.setText(calcColor());
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

    /**
     * Converts the values of the red, green and blue channel into a hexadecimal string
     *
     * @return
     */
    private String calcColor(){
        int buffer = blueValue;
        buffer += (greenValue << 8);
        buffer += (redValue << 16);
        return Integer.toHexString(buffer);
    }

    /**
     * sends the white, red, green and blue channel
     */
    private void sendColors(){
        String buffer = Integer.toHexString(whiteValue);
        buffer += Integer.toHexString(redValue);
        buffer += Integer.toHexString(greenValue);
        buffer += Integer.toHexString(blueValue);
//        MainActivity.bt.send(buffer, false);
    }

}
