package com.zhaw.schoejo2.etp_moodlight;

import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import app.akexorcist.bluetotohspp.library.BluetoothSPP;

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

        MainActivity.bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                if ((data[0] == MainActivity.BT_ALARM) && (data.length >= MainActivity.BT_NORMAL_MESSAGE_LEN)){
                    Toast.makeText(getApplicationContext(), "received alarmtime", Toast.LENGTH_SHORT);
                    int alarmTime = 0;
                    alarmTime += (((int) data[2]) << 24);
                    alarmTime += (((int) data[3]) << 16);
                    alarmTime += (((int) data[4]) << 8);
                    alarmTime += (((int) data[5]) << 0);
                    setTimeText(alarmTime);
                }
            }
        });

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
        timer.start();
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

    public void makeToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
    }

    public void setTimeText(int seconds){
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        timeText.setText(timeToString(hours, minutes));
    }

    public void sendAlarmTimeRequest() {
        byte[] buffer = new byte[7];
        buffer[0] = MainActivity.BT_ALARM;
        buffer[1] = MainActivity.BT_REQUEST;
        buffer[2] = (byte) (0);
        buffer[3] = (byte) (0);
        buffer[4] = (byte) (0);
        buffer[5] = (byte) (0);
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
        if(hours < 10){
            text += "0";
        }
        text += hours;
        text += " : ";
        if (minutes < 10){
            text += "0";
        }
        text += minutes;

        return text;
    }
}
