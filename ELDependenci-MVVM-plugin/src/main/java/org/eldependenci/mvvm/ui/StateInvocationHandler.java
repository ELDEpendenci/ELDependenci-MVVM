package org.eldependenci.mvvm.ui;

import org.bukkit.Bukkit;
import org.eldependenci.mvvm.ELDMVVMPlugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

public final class StateInvocationHandler implements InvocationHandler {

    private final Queue<String> updateQueue = new ConcurrentLinkedDeque<>();
    private final Map<String, Object> stateMap = new ConcurrentHashMap<>();
    private final Consumer<Queue<String>> updateHandler;
    private final boolean manual;
    private boolean autoUpdate;

    public StateInvocationHandler(Consumer<Queue<String>> updateHandler, boolean manual) {
        this.updateHandler = updateHandler;
        this.manual = manual;
        this.autoUpdate = !manual;
    }

    public synchronized boolean isAutoUpdate() {
        return autoUpdate;
    }

    public synchronized void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    private String toPropertyKey(Method method) {
        var methodName = method.getName().substring(3);
        var words = Arrays.stream(methodName.split("(?=\\p{Upper})")).map(word -> word.toLowerCase()).toList();
        return String.join("-", words);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().startsWith("set")) {
            var property = toPropertyKey(method);
            if (args == null || args.length != 1)
                throw new IllegalArgumentException("setter method must have exactly one argument.");
            this.stateMap.put(property, args[0]);
            this.updateQueue.offer(property);
            if (autoUpdate && !manual) {
                this.notifyStateChanged();
            }
            return null;
        } else if (method.getName().startsWith("get")) {
            var property = toPropertyKey(method);
            if (args != null && args.length != 0)
                throw new IllegalArgumentException("getter method must not have any arguments");
            return getState(property, method.getReturnType());
        } else if (method.getName().equals("notifyStateChanged")) {
            this.notifyStateChanged();
            return null;
        } else {
            throw new IllegalArgumentException("unknown method handling: " + method.getName());
        }
    }

    public void notifyStateChanged() {
        runTaskOrNot(() -> {
            this.updateHandler.accept(this.updateQueue);
            this.updateQueue.clear();
        });
    }

    @SuppressWarnings("unchecked")
    public <T> T getState(String property) {
        return (T) stateMap.get(property);
    }

    public <T> T getState(String property, Class<T> type) {
        if (type.isPrimitive()) {
            return getState(property);
        }
        return type.cast(stateMap.get(property));
    }

    private void runTaskOrNot(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(ELDMVVMPlugin.getPlugin(ELDMVVMPlugin.class), runnable);
        }
    }
}
