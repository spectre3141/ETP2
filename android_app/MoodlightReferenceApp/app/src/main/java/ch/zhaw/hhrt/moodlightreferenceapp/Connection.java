package ch.zhaw.hhrt.moodlightreferenceapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Handles connection and data transfer with the Moodlight.
 *
 * @author Hanspeter Hochreutener, hhrt@zhaw.ch
 * @version 0.8 date 21.10.2016
 */
public class Connection {


    /**
     * Connection status
     */
    @SuppressWarnings("WeakerAccess")
    static final int
            UNKNOWN = 11,
            DISCONNECTED = 13,
            TRANSITION = 14,
            CONNECTED = 15,
            ERROR = -1,
            CANCEL = -2;


    /**
     * TAG for "Android monitor" while debugging
     */
    private static final String TAG = Connection.class.getSimpleName();

    /**
     * Device paired?
     */
    private static volatile boolean mPaired = false;
    /**
     * Status of the connection
     */
    private static volatile int mStatus = UNKNOWN;

    /**
     * Data streams, buffers and their handling
     */
    private byte mTxRxDelimiter;
    private OutputStream mOutStream;
    private InputStream mInStream;
    private volatile boolean mStopListenForData;
    private byte[] mDataInBuffer;
    private int mDataInBufferPosition;

    /**
     * calling activity needed for callback functions.
     */
    private MainActivity mActivity;
    /**
     * Bluetooth device and socket
     */
    private BluetoothDevice mBtDevice;
    private BluetoothSocket mBtSocket;


    /**
     * Connection handling
     * The calling activity is informed of the connection status via callback functions.
     *
     * @param activity the calling activity
     */
    Connection(MainActivity activity) {
        mActivity = activity;
    }

    /**
     * Check status of Moodlight connection
     *
     * @return status of Moodlight connection
     */
    int getStatus() {
        return mStatus;
    }


    /**
     * Enable BluetoothAdapter and check if a device with the defined name is paired.
     * Also sets the delimiter for end of data.
     *
     * @return true = BluetoothAdapter enabled and paired device found
     */
    boolean findDevice() {
        String deviceName = mActivity.getString(R.string.device_name);
        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {               // No Bluetooth HW available
            mPaired = false;
        } else {
            if (!mBtAdapter.isEnabled()) {      // Turn Bluetooth on if it is not yet on
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mActivity.startActivityForResult(enableBluetooth, 0);
            }
            Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {     // Check if device is already paired
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(deviceName)) {
                        mBtDevice = device;
                        mPaired = true;
                    }
                }
            }
        }
        mTxRxDelimiter = (byte) mActivity.getResources()
                .getInteger(R.integer.connection_TXRX_delimiter);
        return mPaired;
    }


    /**
     * Connects the Moodlight in a safe asynchronous way
     * and opens input and output data streams.
     * <br>No return value here because the asynchronous task may take a while.
     * The calling activity is informed via callback method connectionStatusChanged()
     *
     * @see MainActivity#connectionStatusChanged connectionStatusChanged()
     */
    void connect() {
        Log.d(TAG, mActivity.getString(R.string.connection_connect));
        new ConnectionTask().execute();
    }

    /**
     * Disconnects the Moodlight
     * and closes input and output data streams.
     * <br>No return value here because this may take a while.
     * The calling activity is informed via callback method connectionStatusChanged()
     *
     * @see MainActivity#connectionStatusChanged connectionStatusChanged()
     */
    void disconnect() {
        Log.d(TAG, mActivity.getString(R.string.connection_disconnect));
        mStatus = TRANSITION;
        mActivity.connectionStatusChanged(mStatus);
        new ConnectionNotification(mActivity, mStatus);
        try {
            mStopListenForData = true;
            mOutStream.close();
            mInStream.close();
            mBtSocket.close();                  // Make sure that the connection is closed
            Log.d(TAG, mActivity.getString(R.string.connection_disconnected));
        } catch (Exception e) {
                    Log.e(TAG, mActivity.getString(R.string.connection_error) + " while closing");
        }
        synchronized (this) {
            try {                               // Wait a while before attempting to connect again
                wait(mActivity.getResources().getInteger(R.integer.connection_wait_close));
            } catch (Exception e) {
                Log.e(TAG, mActivity.getString(R.string.connection_error) + " while waiting");
            }
        }
        mStatus = DISCONNECTED;
        mActivity.connectionStatusChanged(mStatus);
        new ConnectionNotification(mActivity, mStatus);
    }


    /**
     * Send data to the Moodlight
     *
     * @param data the data to be sent
     * @return true = sending was successful
     */
    boolean sendData(String data) {
        boolean success = false;
        data += (char) mTxRxDelimiter;          // append delimiter character
        try {
            Log.d(TAG, mActivity.getString(R.string.connection_TX_data) + data);
            mOutStream.write(data.getBytes());
            success = true;
        } catch (Exception e) {
            Log.e(TAG, mActivity.getString(R.string.connection_error) + " while sending");
        }
        synchronized (this) {
            try {                               // wait = give Moodlight time for processing data
                wait(mActivity.getResources().getInteger(R.integer.connection_wait_TXRX));
            } catch (Exception e) {
                Log.e(TAG, mActivity.getString(R.string.connection_error) + " while waiting");
            }
        }
        return success;
    }

    /**
     * Listen for data from the Moodlight.
     * <br>When a String with a defined delimiter was received
     * the method receivedData() in the calling activity is called
     *
     * @see MainActivity#receivedData receivedData()
     */
    private void listenForData() {
        final Handler rxMessageHandler = new Handler();
        // Handler needed to access outer class from inner Thread
        mStopListenForData = false;             // stop inner Thread flag
        mDataInBuffer = new byte[256];          // temporary buffer
        mDataInBufferPosition = 0;              // position of last byte in the buffer
        Thread dataInThread = new Thread(       // Thread listens for incoming data
                new Runnable() {                // Uses inner classes
                    public void run() {
                        while (!Thread.currentThread().isInterrupted() && !mStopListenForData) {
                            try {
                                int bytesAvailable = mInStream.available();
                                if (bytesAvailable > 0) {
                                    byte[] packetBytes = new byte[bytesAvailable];
                                                // Temporary variable for InputStream
                                    bytesAvailable = mInStream.read(packetBytes);
                                    for (int i = 0; i < bytesAvailable; i++) {
                                        if (mTxRxDelimiter == packetBytes[i]) {
                                                // Delimiter character was received
                                            final String data = new String(mDataInBuffer, 0,
                                                    mDataInBufferPosition);
                                                // Make a String from received bytes
                                            mDataInBufferPosition = 0;  // Start anew with next byte
                                            rxMessageHandler.post(
                                                    new Runnable() {    // Process received String
                                                        public void run() {
                                                            Log.d(TAG, mActivity.getString(R.string.connection_RX_data) + data);
                                                            mActivity.receivedData(data);
                                                        }
                                                    }
                                            );
                                        } else {    // Normal character was received
                                            mDataInBuffer[mDataInBufferPosition++] = packetBytes[i];
                                                // Store the received byte in the temporary buffer
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, mActivity.getString(R.string.connection_error) + " listenForData runnable");
                                mStopListenForData = true;
                            }
                            synchronized (this) {
                                try {           // Wait a while before listening again
                                    wait(mActivity.getResources().getInteger(R.integer.connection_wait_TXRX));
                                } catch (Exception e) {
                                    Log.e(TAG, mActivity.getString(R.string.connection_error) + " while waiting");
                                }
                            }

                        }
                    }
                }
        );
        dataInThread.start();
    }


    /**
     * Connect to the Moodlight.
     * AsyncTask runs in the background without blocking the UI thread.
     */
    private class ConnectionTask extends AsyncTask<Integer, Integer, Integer> {
        /**
         * The background task.
         *
         * @param params are passed when calling ConnectionTask().execute(params)
         * @return result is passed to onPostExecute(result)
         */
        @Override
        protected Integer doInBackground(Integer... params) {
            Boolean success = false;
            int tryCount = 0;
            while (!success && tryCount < mActivity.getResources().getInteger(R.integer.connection_retry)) {
                tryCount++;
                publishProgress(tryCount);      // progress passed to onProgressUpdate()
                try {
                    UUID uuid = UUID.fromString(mActivity.getString(R.string.connection_UUID));  // SerialPortService ID
                    mBtSocket = mBtDevice.createRfcommSocketToServiceRecord(uuid);
                    mBtSocket.connect();        // Try to establishing the connection
                    success = true;
                    Log.d(TAG, mActivity.getString(R.string.connection_connected) + " at try nr. " + tryCount);
                } catch (Exception e) {
                    Log.e(TAG, mActivity.getString(R.string.connection_error) + " at try nr. " + tryCount);
                }
                if (!success) {
                    disconnect();               // Disconnect before next try
                }
            }
            if (!success) {
                return ERROR;
            } else {
                return CONNECTED;
            }
        }

        /**
         * Called by publishProgress(progress) in doInBackground(params).
         *
         * @param progress passed from publishProgress(progress)
         */
        @Override
        protected void onProgressUpdate(Integer... progress) {
        }

        /**
         * Executed before doInBackground(params).
         */
        @Override
        protected void onPreExecute() {
            mStatus = TRANSITION;
            mActivity.connectionStatusChanged(mStatus);
            new ConnectionNotification(mActivity, mStatus);
        }

        /**
         * Executed after doInBackground(params).
         *
         * @param result return value of doInBackground(params)
         */
        protected void onPostExecute(Integer result) {
            if (result == CONNECTED) {
                try {
                    mOutStream = mBtSocket.getOutputStream();
                    mInStream = mBtSocket.getInputStream();
                    listenForData();            // Start a new Thread which handles incomming data
                    mStatus = CONNECTED;
                } catch (Exception e) {
                    Log.e(TAG, mActivity.getString(R.string.connection_error) + " while opening streams");
                    mStatus = ERROR;
                }
            } else {
                mStatus = ERROR;
            }
            mActivity.connectionStatusChanged(mStatus);
            new ConnectionNotification(mActivity, mStatus);
        }
    }


    /**
     * Notifications for the connection status.
     */
    private class ConnectionNotification {

        /**
         * Issues a new connection notification or cancels an existing one.
         *
         * @param activity     from which the notification is issued
         * @param notification defines which icon and text is issued;
         * @see #CANCEL CANCEL removes an existing notification
         */
        ConnectionNotification(Activity activity, int notification) {
            final int NOTIFICATION_ID = 729;    // just a number
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity);
            // mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
            // mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
            switch (notification) {
                case CANCEL: {
                    NotificationManager mNotifyMgr =
                            (NotificationManager) activity.getSystemService(Activity.NOTIFICATION_SERVICE);
                    mNotifyMgr.cancel(NOTIFICATION_ID);
                    return;                     // cancel notification and exit
                }
                case CONNECTED: {
                    mBuilder.setSmallIcon(R.drawable.ic_sync_black_24dp)
                            .setContentTitle(activity.getString(R.string.app_name))
                            .setContentText(activity.getString(R.string.connection_connected));
                    break;
                }
                case DISCONNECTED: {
                    mBuilder.setSmallIcon(R.drawable.ic_sync_disabled_black_24dp)
                            .setContentTitle(activity.getString(R.string.app_name))
                            .setContentText(activity.getString(R.string.connection_disconnected));
                    break;
                }
                case TRANSITION: {
                    mBuilder.setSmallIcon(R.drawable.ic_sync_problem_black_24dp)
                            .setContentTitle(activity.getString(R.string.app_name))
                            .setContentText(activity.getString(R.string.connection_transition));
                    break;
                }
                case ERROR: {
                    mBuilder.setSmallIcon(R.drawable.ic_sync_problem_black_24dp)
                            .setContentTitle(activity.getString(R.string.app_name))
                            .setContentText(activity.getString(R.string.connection_error));
                    break;
                }
                default:
                    Toast.makeText(activity, activity.getString(R.string.connection_error),
                            Toast.LENGTH_LONG).show();
            }
            NotificationManager mNotifyMgr =
                    (NotificationManager) activity.getSystemService(Activity.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());

        }
    }


}
