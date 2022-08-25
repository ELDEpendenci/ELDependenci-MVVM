package org.eldependenci.mvvm.ui;

import org.eldependenci.mvvm.viewmodel.UISession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public final class MVVMUISession implements UISession {

    private final Map<String, Object> containers = new ConcurrentHashMap<>();

    @Override
    public <T> T pollAttribute(String key) {
        return (T)containers.remove(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        containers.put(key, value);
    }

    @Override
    public <T> T getAttribute(String key) {
        return (T)containers.get(key);
    }
}
