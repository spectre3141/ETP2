package com.zhaw.schoejo2.etp_moodlight;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothConnectionListener;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.OnDataReceivedListener;

public class MainActivity extends AppCompatActivity {

    // Bluetooth connected variables
    public static BluetoothSPP bt;

    // Activities:
    LedActivity ledActivity = new LedActivity();
    AlarmActivity alarmActivity = new AlarmActivity();

    // GUI-Element Instances
    Button ledButton;
    Button alarmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        bt = new BluetoothSPP(this);

        // bluetooth is not available
        if (!bt.isBluetoothAvailable())
        {
            //finish();
        }

        if (bt.isBluetoothEnabled()){
            bt.startService(BluetoothState.DEVICE_OTHER);
        } else {
            // TODO: connection failed
        }

        bt.setOnDataReceivedListener(new OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                // TODO: insert Listener actions
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                // TODO
            }

            @Override
            public void onDeviceDisconnected() {
                // TODO
            }

            @Override
            public void onDeviceConnectionFailed() {
                // TODO
            }
        });*/

        /* Configuration of GUI-Elements */
        ledButton = (Button) findViewById(R.id.ledButton);
        ledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ledActivity.getClass()));
            }
        });

        alarmButton = (Button) findViewById(R.id.alarmButton);
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, alarmActivity.getClass()));
            }
        });

    }

    @Override
    public void onStart()
    {
        super.onStart();

    }

}
