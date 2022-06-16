package org.eldependenci.mvvm.viewmodel;

public interface UISession {

    <T> T pollAttribute(String key);

    void setAttribute(String key, Object value);

    <T> T getAttribute(String key);

}
