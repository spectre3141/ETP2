package com.zhaw.schoejo2.etp_moodlight;

import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class AlarmActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, AlarmRequestTimerListener  {

    // constants
    final private int ALARM_UPDATE_TIME_MS = 10000; // = 10s

    // alarm update timer
    AlarmRequestTimer timer;

    // GUI instances
    TextView timeText;
    Button timeButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // GUI initialization
        timeText = (TextView) findViewById(R.id.timeText);
        timeButton = (Button) findViewById(R.id.timeButton);

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "timepicker");
            }
        });

    }



    @Override
    public void onStart() {
        super.onStart();
        timer = new AlarmRequestTimer(this, ALARM_UPDATE_TIME_MS);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            timer.kill();
        } catch (Exception e) {
            // necessary to avoid errors if for some reason the thread does not exist
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int seconds = (hourOfDay * 3600) + (minute * 60);
        timeText.setText(timeToString(hourOfDay, minute));
        sendAlarmTime(seconds);
    }

    /**
     * sends the moodlight a request to send the current alarm time back
     * calls sendAlarmTimeRequest()
     */
    public void timeElapsed() {
        sendAlarmTimeRequest();
    }

    public void setTimeText(int seconds){
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        timeText.setText(timeToString(hours, minutes));
    }

    private void sendAlarmTimeRequest() {
        byte[] buffer = new byte[7];
        buffer[0] = MainActivity.BT_ALARM;
        buffer[1] = MainActivity.BT_REQUEST;
        buffer[2] = 0x00;
        buffer[3] = 0x00;
        buffer[4] = 0x00;
        buffer[5] = 0x00;
        buffer[6] = MainActivity.BT_DELIMITER;
        MainActivity.bt.send(buffer, false);
    }

    private void sendAlarmTime(int value){
        byte[] buffer = new byte[7];
        buffer[0] = MainActivity.BT_ALARM;
        buffer[1] = MainActivity.BT_SEND;
        buffer[2] = (byte) ((value >> 24) & 0xFF);
        buffer[3] = (byte) ((value >> 16) & 0xFF);
        buffer[4] = (byte) ((value >> 8) & 0xFF);
        buffer[5] = (byte) ((value >> 0) & 0xFF);
        buffer[6] = MainActivity.BT_DELIMITER;
        MainActivity.bt.send(buffer, false);
    }

    private String timeToString(int hours, int minutes){
        String text = "";
        if (hours == 0){
            text += "00";
        } else if(hours < 10){
            text += "0";
        }
        text += hours;
        text += " : ";
        if (minutes == 0) {
            text += "00";
        } else if (minutes < 10){
            text += "0";
        }
        text += minutes;

        return text;
    }
}
