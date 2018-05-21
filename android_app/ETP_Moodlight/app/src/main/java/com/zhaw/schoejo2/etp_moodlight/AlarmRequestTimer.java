package com.zhaw.schoejo2.etp_moodlight;

public class AlarmRequestTimer extends Thread {

    private boolean running;
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
                sleep(time);
            } catch (InterruptedException e){
                // do nothing (timer doesn't have to be that precise
            }
        }
    }

}
