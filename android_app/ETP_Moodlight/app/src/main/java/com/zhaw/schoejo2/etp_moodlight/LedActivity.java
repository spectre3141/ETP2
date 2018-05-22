package com.zhaw.schoejo2.etp_moodlight;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

public class LedActivity extends AppCompatActivity {

    RenameActivity renameActivity = new RenameActivity();

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

        colorA0Button = (Button) findViewById(R.id.colorA0Button);
        colorA1Button = (Button) findViewById(R.id.colorA1Button);
        colorA2Button = (Button) findViewById(R.id.colorA2Button);
        colorA3Button = (Button) findViewById(R.id.colorA3Button);
        colorA4Button = (Button) findViewById(R.id.colorA4Button);
        colorA5Button = (Button) findViewById(R.id.colorA5Button);
        colorB0Button = (Button) findViewById(R.id.colorB0Button);
        colorB1Button = (Button) findViewById(R.id.colorB1Button);
        colorB2Button = (Button) findViewById(R.id.colorB2Button);
        colorB3Button = (Button) findViewById(R.id.colorB3Button);
        colorB4Button = (Button) findViewById(R.id.colorB4Button);
        colorB5Button = (Button) findViewById(R.id.colorB5Button);
        colorC0Button = (Button) findViewById(R.id.colorC0Button);
        colorC1Button = (Button) findViewById(R.id.colorC1Button);
        colorC2Button = (Button) findViewById(R.id.colorC2Button);
        colorC3Button = (Button) findViewById(R.id.colorC3Button);
        colorC4Button = (Button) findViewById(R.id.colorC4Button);
        colorC5Button = (Button) findViewById(R.id.colorC5Button);
        colorD0Button = (Button) findViewById(R.id.colorD0Button);
        colorD1Button = (Button) findViewById(R.id.colorD1Button);
        colorD2Button = (Button) findViewById(R.id.colorD2Button);
        colorD3Button = (Button) findViewById(R.id.colorD3Button);
        colorD4Button = (Button) findViewById(R.id.colorD4Button);
        colorD5Button = (Button) findViewById(R.id.colorD5Button);

        ledCommitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buffer = colorByValueEdit.getText().toString();
                Toast.makeText(getApplicationContext(), buffer, Toast.LENGTH_SHORT).show();
                if (stringToColor(buffer)){
                    setSeekbars();
                    sendColors();
                } else if(buffer.contains("42")){
                    startActivity(new Intent(LedActivity.this, renameActivity.getClass()));
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
                sendColors();
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
                sendColors();
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
                sendColors();
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
                sendColors();
            }
        });

        colorA0Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorA0)));
        colorA1Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorA1)));
        colorA2Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorA2)));
        colorA3Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorA3)));
        colorA4Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorA4)));
        colorA5Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorA5)));
        colorB0Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorB0)));
        colorB1Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorB1)));
        colorB2Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorB2)));
        colorB3Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorB3)));
        colorB4Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorB4)));
        colorB5Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorB5)));
        colorC0Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorC0)));
        colorC1Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorC1)));
        colorC2Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorC2)));
        colorC3Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorC3)));
        colorC4Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorC4)));
        colorC5Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorC5)));
        colorD0Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorD0)));
        colorD1Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorD1)));
        colorD2Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorD2)));
        colorD3Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorD3)));
        colorD4Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorD4)));
        colorD5Button.setOnClickListener(new ColorButtonListener(getColor(R.color.colorD5)));

        sendColors();

    }

    private void splitColor(int value){
        redValue = ((value >> 16) & 0xFF);
        greenValue = ((value >> 8) & 0xFF);
        blueValue = (value & 0xFF);
    }

    /**
     * Converts the values of the red, green and blue channel into a hexadecimal string
     *
     * @return
     */
    private String calcColor(){
        String buffer;
        if (redValue == 0){
            buffer = "00";
        } else if (redValue < 0x10){
            buffer = "0" + Integer.toHexString(redValue);
        } else {
            buffer = Integer.toHexString(redValue);
        }
        if (greenValue == 0){
            buffer += "00";
        } else if (greenValue < 0x10){
            buffer += "0" + Integer.toHexString(greenValue);
        } else {
            buffer += Integer.toHexString(greenValue);
        }
        if (blueValue == 0){
            buffer += "00";
        } else if (blueValue < 0x10){
            buffer += "0" + Integer.toHexString(blueValue);
        } else {
            buffer += Integer.toHexString(blueValue);
        }
        return buffer;
    }

    /**
     * Converts String of length 6 into 3 integers and stores
     * the values in the variables redValue, greenValue, blueValue
     * returns false if the String length is invalid
     *
     * @param color
     * @return
     */
    private boolean stringToColor(String color){
        if (color.length() == 6){
            redValue = Integer.decode(HEX_FORMAT + color.substring(0, 2));
            greenValue = Integer.decode(HEX_FORMAT + color.substring(2, 4));
            blueValue = Integer.decode(HEX_FORMAT + color.substring(4, 6));
            return true;
        } else
            return false;
    }

    private void setSeekbars(){
        redSeekbar.setProgress(redValue);
        greenSeekbar.setProgress(greenValue);
        blueSeekbar.setProgress(blueValue);
        whiteSeekbar.setProgress(whiteValue);
    }

    /**
     * sends the white, red, green and blue channel
     */
    private void sendColors(){
        // control bytes (0: data for LED, x: all channels
        byte[] buffer = new byte[7];
        buffer[0] = MainActivity.BT_LED;
        buffer[1] = MainActivity.BT_SEND;
        // add channel values
        buffer[2] = (byte) redValue;
        buffer[3] = (byte) greenValue;
        buffer[4] = (byte) blueValue;
        buffer[5] = (byte) whiteValue;
        // add frame delimiter
        buffer[6] = MainActivity.BT_DELIMITER;
        MainActivity.bt.send(buffer, false);
    }

    /**
     * dedicated listener class to lower effort on colorButton listeners
     */
    private class ColorButtonListener implements View.OnClickListener {

        private int color;

        public ColorButtonListener(int color){
            this.color = color;
        }

        @Override
        public void onClick(View v) {
            splitColor(color);
            colorByValueEdit.setText(calcColor());
            setSeekbars();
            sendColors();
        }
    }

}
