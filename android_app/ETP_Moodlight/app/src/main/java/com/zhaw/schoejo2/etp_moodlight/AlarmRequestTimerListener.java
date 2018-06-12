package com.zhaw.schoejo2.etp_moodlight;

/**
 * The AlarmRequestTimer calls this function every cycle
 */
public interface AlarmRequestTimerListener {
    abstract public void timeElapsed();
    abstract public void makeToast(String text);
}
