package org.eldependenci.mvvm.demo.time;

import org.eldependenci.mvvm.model.StateHolder;

public interface TimeStateHolder extends StateHolder {

    String getTime();

    void setTime(String time);

    long getDuration();

    void setDuration(long duration);

}
