package com.zhaw.schoejo2.etp_moodlight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmActivity extends AppCompatActivity implements TimerListener {

    // constants
    final private int ALARM_UPDATE_TIME = 10000; // > 10s

    // alarm update timer
    AlarmRequestTimer timer;

    // GUI instances
    TextView alarmInfoText;
    EditText alarmTimeEdit;
    Button alarmCommitButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // GUI initialization
        alarmInfoText = (TextView) findViewById(R.id.alarmInfoText);
        alarmTimeEdit = (EditText) findViewById(R.id.alarmTimeEdit);
        alarmCommitButton = (Button) findViewById(R.id.alarmCommitButton);

        alarmCommitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int timeValue = 0;  // in seconds
                int hours = 0;
                int minutes = 0;
                int seconds = 0;
                String buffer[] = alarmTimeEdit.getText().toString().split("[:.-]");
                switch (buffer.length) {
                    case 1:
                        seconds = Integer.valueOf(buffer[0]);
                        break;
                    case 2:
                        seconds = Integer.valueOf(buffer[1]);
                        minutes = Integer.valueOf(buffer[0]);
                        break;
                    case 3:
                        seconds = Integer.valueOf(buffer[2]);
                        minutes = Integer.valueOf(buffer[1]);
                        hours = Integer.valueOf(buffer[0]);
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "bad input, please use the format hh:mm:ss", Toast.LENGTH_SHORT).show();
                        break;
                }
                if ((seconds < 0) || (seconds >= 60)){

                }
                // TODO: send value to moodlight
/*                // test:
                String text = "";
                text += String.valueOf(buffer[0]);
                text += ":";
                text += String.valueOf(buffer[1]);
                alarmInfoText.setText(text);
*/
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        timer = new AlarmRequestTimer(this, ALARM_UPDATE_TIME);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            timer.kill();
        } catch (Exception e) {
            // necessary to avoid errors if the thread doesnt run/exist
        }
    }

    private void sendAlarmTimeRequest() {
        // TODO: insert function
    }

    /**
     * sends the moodlight a request to send the current alarm time back
     * calls sendAlarmTimeRequest()
     */
    public void timeElapsed() {
        sendAlarmTimeRequest();
    }

}
