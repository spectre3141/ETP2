package com.zhaw.schoejo2.etp_moodlight;

import android.widget.Toast;

public class AlarmRequestTimer extends Thread {

    private boolean running = true;
    private AlarmRequestTimerListener listener;
    private int time;

    /**
     * Constructor
     *
     * @param listener
     * @param time_ms
     */
    public AlarmRequestTimer(AlarmRequestTimerListener listener, int time_ms){
        this.listener = listener;
        this.time = time_ms;
    }

    public void kill(){
        running = false;
    }

    public void run(){
        while(running){
            listener.timeElapsed();
            try{
                Thread.sleep(time);
            } catch (Exception e){
                // do nothing (timer doesn't have to be that precise
            }
        }
    }

}
