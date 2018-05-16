package com.zhaw.schoejo2.etp_moodlight;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothConnectionListener;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.OnDataReceivedListener;

public class MainActivity extends AppCompatActivity {

    // constants
    final static String BT_DELIMITER = String.valueOf(0xD);
    final static String BT_LED = "0";
    final static String BT_ALARM = "1";
    final static String BT_SEND = ">";
    final static String BT_REQUEST = "<";

    // Bluetooth connected variables
    public static BluetoothSPP bt;

    // Activities:
    LedActivity ledActivity = new LedActivity();
    AlarmActivity alarmActivity = new AlarmActivity();

    // GUI-Element Instances
    Button ledButton;
    Button alarmButton;
    Button bluetoothButton;
    TextView bluetoothText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt = new BluetoothSPP(this);

        if (!bt.isBluetoothAvailable())
        {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
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
                bluetoothText.setText(bt.getConnectedDeviceName());
            }

            @Override
            public void onDeviceDisconnected() {
                bluetoothText.setText("Device disconnected");
            }

            @Override
            public void onDeviceConnectionFailed() {
                bluetoothText.setText("Connection failed");
            }
        });

        // GUI initialization
        ledButton = (Button) findViewById(R.id.ledButton);
        alarmButton = (Button) findViewById(R.id.alarmButton);
        bluetoothButton = (Button) findViewById(R.id.bluetoothButton);
        bluetoothText = (TextView) findViewById(R.id.bluetoothText);

        bluetoothText.setText("not connected");
        ledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ledActivity.getClass()));
            }
        });

        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, alarmActivity.getClass()));
            }
        });

        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);                    //no Android device -> DEVICE_OTHER
                if(bt.getServiceState() == BluetoothState.STATE_CONNECTED) {        //check if bluetooth is already connected to something
                    bt.disconnect();
                }
                Intent intent = new Intent(getApplicationContext(), DeviceList.class);  //open DeviceList-Page from BT_Lib
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }
        });

    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK) {
                bt.connect(data);
            }
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
            }  else {
                Toast.makeText(getApplicationContext(), "Bluetooth was not enabled.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}


