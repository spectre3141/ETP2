package ch.zhaw.hhrt.moodlightreferenceapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;


/**
 * Red-Green-Blue+White view (fragment) of the app.
 * Standard user interface where the brightness of each coloured LED can be adjusted individually
 * by changing the position of the assigned SeekBar.
 * <br>
 * <img src="{@docRoot}/images/RgbwFragment.png" alt="screenshot">
 *
 * @author Hanspeter Hochreutener, hhrt@zhaw.ch
 * @version 0.8 date 21.10.2016
 */
public class RgbwFragment extends Fragment {


    /**
     * Empty constructor is required
     */
    public RgbwFragment() {
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
        View view = inflater.inflate(R.layout.fragment_rgbw, container, false);
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
        changeSeekBarListener(R.id.seekbar_white, getString(R.string.label_white));
        changeSeekBarListener(R.id.seekbar_red, getString(R.string.label_red));
        changeSeekBarListener(R.id.seekbar_green, getString(R.string.label_green));
        changeSeekBarListener(R.id.seekbar_blue, getString(R.string.label_blue));
    }


    /**
     * Change the SeekBar positions according to the received data
     *
     * @param data that has been received
     */
    public void receivedData(String data) {
        setSeekBarPositionFromData(data);
    }

    /**
     * Change/register listeners for the SeekBars which change the brightness of the coloured LEDs.
     * This has to be done once, after the view of this fragment has been created.
     * When the user touches a SeekBar, the new position is sent to the Moodlight.
     *
     * @param id    of the SeekBar
     * @param label string to be sent to the Moodlight when this SeekBar has been moved
     */
    private void changeSeekBarListener(int id, final String label) {
        @SuppressWarnings("ConstantConditions")
        SeekBar bar = (SeekBar) getView().findViewById(id);
        bar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {     // Uses an inner anonymous class
                    public void onProgressChanged(SeekBar SeekBar, int progress, boolean fromUser) {
                        /* Send new SeekBar position to the Moodlight
                        only if the user has touched the SeekBar
                        and not if the position has been changed programmatically.
                        */
                        if (fromUser) {
                            String data = label + " " + SeekBar.getProgress();
                            ((MainActivity) getActivity()).sendData(data);
                        }
                    }

                    public void onStartTrackingTouch(SeekBar SeekBar) {
                    }

                    public void onStopTrackingTouch(SeekBar SeekBar) {
                    }
                }
        );
    }


    /**
     * Set new position of a SeekBar.
     * The SeekBar and its position are specified in the received String.
     *
     * @param data received from the Moodlight
     */
    private void setSeekBarPositionFromData(String data) {
        int id;
        String command = "";
        int value = 0;
        String[] words = data.split("\\s");     // Split the message at white-space characters
        if (words.length > 0) {
            command = words[0];                 // First word is interpreted as command
        }
        if (words.length > 1) {                 // Is there a second word?
            try {
                value = Integer.parseInt(words[1].trim());  // Second word is interpreted as number
            } catch (NumberFormatException e) {
                value = 0;
            }
        }
        /* Compare the received command with each possible value
         * to find out which SeekBar has to be adjusted.
         * Normally this would be programmed with: switch (command) case ...
         * As switch can not compare to string resources I used: if ... else if ...
         */
        if (command.equals(getString(R.string.label_red))) {
            id = R.id.seekbar_red;
        } else if (command.equals(getString(R.string.label_green))) {
            id = R.id.seekbar_green;
        } else if (command.equals(getString(R.string.label_blue))) {
            id = R.id.seekbar_blue;
        } else if (command.equals(getString(R.string.label_white))) {
            id = R.id.seekbar_white;
        } else if (command.equals(getString(R.string.label_idle))) {
            id = 0;                             // Nothing to do
        } else {
            id = 0;                             // Unknown command
        }
        if ((id != 0) && (words.length > 1)) {  // Valid command and value were received
            @SuppressWarnings("ConstantConditions")
            SeekBar bar = (SeekBar) getView().findViewById(id);
            bar.setProgress(value);
        }
    }

}
