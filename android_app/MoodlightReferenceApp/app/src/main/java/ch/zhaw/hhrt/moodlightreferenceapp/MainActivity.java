package ch.zhaw.hhrt.moodlightreferenceapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


/**
 * Command center of the app.
 * Manage the fragments of the user interface and the connection with the Moodlight.
 * <br>
 * <img src="{@docRoot}/images/UmlSequenceDiagram.png" alt="screenshot">
 * <br>
 * <p>
 * <b>Tips for communication between the MainActivity and its fragments:</b>
 * <pre>// Call a method in the MainActivity from within a fragment:
 * ((MainActivity) getActivity()).methodInMainActivity();</pre>
 * <pre>// Call a method in a fragment from within the MainActivity:
 * fragmentInstanceName.methodInFragment();</pre>
 *
 * @author Hanspeter Hochreutener, hhrt@zhaw.ch
 * @version 0.8 date 21.10.2016
 */
public class MainActivity extends AppCompatActivity {


    /**
     * TAG for "Android monitor" while debugging: Log.d(TAG, "message");
     */
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Fragment with the standard user interface
     */
    private RgbwFragment mRgbwFragment = new RgbwFragment();
    /**
     * Fragment with the service user interface
     */
    private ServiceFragment mServiceFragment = new ServiceFragment();
    /**
     * Connection to the Moodlight
     */
    private Connection mConnection = new Connection(this);


    /**
     * Called when the app is created.
     * Add the components of the userinterface.
     *
     * @param savedInstanceState state of the app before it was stopped the last time
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            // Add the first fragment to the 'fragment_container'
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mRgbwFragment).commit();
        }
        findDevice();                           // Check if Moodlight device is already paired
    }


    /**
     * Initialize the standard menu.
     * Called once at startup.
     *
     * @param menu menu items
     * @return true = menu displayed
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    /**
     * Define menu actions
     *
     * @param item selected menu item
     * @return true = menu item has been processed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, item.toString());
        int id = item.getItemId();
        if (id == R.id.action_rgbw) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, mRgbwFragment);
            transaction.commit();
            synchData();                        // Synchronize the SeekBars with the Moodlight
            return true;
        }
        if (id == R.id.action_service) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, mServiceFragment);
            transaction.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);   // Any other item is passed to the superclass
    }


    /**
     * Called before the app is displayed.
     * Opens the connection to the Moodlight.
     */
    @Override
    public void onStart() {
        super.onStart();
        connect();
    }


    /**
     * Called when the app is hidden (or closed).
     * Closes the connection to the Moodlight.
     */
    @Override
    public void onStop() {
        disconnect();
        super.onStop();
    }


    /**
     * Find the paired Moodlight.
     */
    void findDevice() {
        if (mConnection.findDevice()) {
            Log.d(TAG, getString(R.string.connection_paired));
        } else {
            Log.e(TAG, getString(R.string.connection_not_paired));
            Toast.makeText(this, getString(R.string.connection_not_paired), Toast.LENGTH_LONG)
                    .show();
            finish();                           // Finish app because Moodlight is not paired
        }
    }


    /**
     * Connect to the Moodlight.
     */
    void connect() {
        Log.d(TAG, getString(R.string.connection_connect));
        mConnection.connect();
    }


    /**
     * Disconnect from the Moodlight.
     */
    void disconnect() {
        Log.d(TAG, getString(R.string.connection_disconnect));
        mConnection.disconnect();
    }


    /**
     * Called when connection status has changed.
     *
     * @param result which has been passed from (dis)connection task
     * @see #synchData() synchData() which is called when RgbwFragment is active
     */
    void connectionStatusChanged(int result) {
        Log.d(TAG, getString(R.string.connection_transition) + ": " + result);
        Fragment f = this.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if ((f == mRgbwFragment) && (result == Connection.CONNECTED)) {
            synchData();
        }
    }


    /**
     * Send data to the Moodlight.
     *
     * @param data the String to be sent.
     */
    void sendData(String data) {
        Log.d(TAG, getString(R.string.connection_TX_data) + data);
        if (Connection.CONNECTED == mConnection.getStatus()) {
            if (!mConnection.sendData(data)) {
                Toast.makeText(this, getString(R.string.connection_not_connected), Toast.LENGTH_SHORT)
                        .show();
            }
        } else {    // Synchronization not possible when not connected
            Toast.makeText(this, getString(R.string.connection_not_connected), Toast.LENGTH_SHORT)
                    .show();
        }
    }


    /**
     * Process data received from the Moodlight.
     * The data is passed to the active fragment for contextual processing.
     *
     * @param data the String that was received.
     */
    void receivedData(String data) {
        Log.d(TAG, getString(R.string.connection_RX_data) + data);
        Fragment f = this.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f == mRgbwFragment) {
            mRgbwFragment.receivedData(data);
        }
        if (f == mServiceFragment) {
            mServiceFragment.receivedData(data);
        }
    }


    /**
     * Synchronize the values in the App with those in the Moodlight.
     */
    void synchData() {
        if (Connection.CONNECTED == mConnection.getStatus()) {
            sendData(getString(R.string.label_red) + " ?");
            sendData(getString(R.string.label_green) + " ?");
            sendData(getString(R.string.label_blue) + " ?");
            sendData(getString(R.string.label_white) + " ?");
            sendData(getString(R.string.label_idle));
        } else {                                // Synchronization not possible when not connected
            Toast.makeText(this, getString(R.string.connection_not_connected), Toast.LENGTH_SHORT)
                    .show();
        }

    }


}
