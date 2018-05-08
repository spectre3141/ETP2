package ch.zhaw.hhrt.moodlightreferenceapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * Service view (fragment) of the app.
 * User interface where the communication to the Moodlight can be controlled manually.
 * <br>
 * <img src="{@docRoot}/images/ServiceFragment.png" alt="screenshot">
 *
 * @author Hanspeter Hochreutener, hhrt@zhaw.ch
 * @version 0.8 date 21.10.2016
 */
@SuppressWarnings("ConstantConditions") // findViewById may produce NullPointerException
public class ServiceFragment extends Fragment {


    /**
     * Empty constructor is required
     */
    public ServiceFragment() {
    }

    /**
     * Called when the fragment is created.
     *
     * @param savedInstanceState state of the app before it was stopped the last time
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Add the components to the view.
     *
     * @param inflater           the LayoutLnflator
     * @param container          the ViewGroup
     * @param savedInstanceState saved state, if available
     * @return the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        @SuppressWarnings("FieldCanBeLocal")
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        return view;
    }

    /**
     * Called after "onCreateView()".
     * As the components of the view are available only now
     * the Listeners cannot be attached in "onCreate()" or "onCreateView()".
     *
     * @param view               the active view
     * @param savedInstanceState saved state, if available
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changeDisconnectListener();
        changeConnectListener();
        changeSynchButtonListener();
        changeSendListener();
    }


    /**
     * Display the received data
     *
     * @param data that has been received
     */
    public void receivedData(String data) {
        ((TextView) getView().findViewById(R.id.message_RX)).setText(data);
    }


    /**
     * Change/register listener for the Disconnect button
     */
    private void changeDisconnectListener() {
        Button button = (Button) getView().findViewById(R.id.button_disconnect);
        button.setOnClickListener(
                new Button.OnClickListener() {     // Uses an inner anonymous class
                    public void onClick(View view) {
                        ((MainActivity) getActivity()).disconnect();
                    }
                });
    }

    /**
     * Change/register listener for the Connect button
     */
    private void changeConnectListener() {
        Button button = (Button) getView().findViewById(R.id.button_connect);
        button.setOnClickListener(
                new Button.OnClickListener() {     // Uses an inner anonymous class
                    public void onClick(View view) {
                        ((MainActivity) getActivity()).connect();
                    }
                });
    }

    /**
     * Change/register listener for the Synchronize button
     */
    private void changeSynchButtonListener() {
        Button button = (Button) getView().findViewById(R.id.button_synch);
        button.setOnClickListener(
                new Button.OnClickListener() {     // Uses an inner anonymous class
                    public void onClick(View view) {
                        ((MainActivity) getActivity()).synchData();
                    }
                });
    }


    /**
     * Change/register listener for the Send button
     */
    private void changeSendListener() {
        Button button = (Button) getView().findViewById(R.id.button_send);
        button.setOnClickListener(
                new Button.OnClickListener() {     // Uses an inner anonymous class
                    public void onClick(View view) {
                        String data = ((TextView) getView().findViewById(R.id.message_TX))
                                .getText().toString();
                        ((MainActivity) getActivity()).sendData(data);
                    }
                });
    }


}
