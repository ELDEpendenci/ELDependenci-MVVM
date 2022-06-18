package org.eldependenci.mvvm.ui;

import org.bukkit.Bukkit;
import org.eldependenci.mvvm.ELDMVVMPlugin;

import javax.lang.model.type.PrimitiveType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

public final class StateInvocationHandler implements InvocationHandler {


    private final Queue<String> updateQueue = new ConcurrentLinkedDeque<>();
    private final Map<String, Object> stateMap = new ConcurrentHashMap<>();


    private final Consumer<String> onPropertyUpdate;
    private final Consumer<Queue<String>> manualUpdate;
    private final boolean manual;


    public StateInvocationHandler(Consumer<String> onPropertyUpdate, Consumer<Queue<String>> manualUpdate, boolean manual) {
        this.onPropertyUpdate = onPropertyUpdate;
        this.manualUpdate = manualUpdate;
        this.manual = manual;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().startsWith("set")) {
            var property = method.getName().substring(3).toLowerCase();
            if (args == null || args.length != 1) throw new IllegalArgumentException("setter method must have exactly one argument.");
            this.stateMap.put(property, args[0]);
            if (!manual) {
                runTaskOrNot(() -> this.onPropertyUpdate.accept(property));
            } else {
                this.updateQueue.offer(property);
            }
            return null;
        } else if (method.getName().startsWith("get")) {
            var property = method.getName().substring(3).toLowerCase();
            if (args != null && args.length != 0) throw new IllegalArgumentException("getter method must not have any arguments");
            return getState(property, method.getReturnType());
        } else if (method.getName().equals("notifyStateChanged")) {
            runTaskOrNot(() -> {
                this.manualUpdate.accept(this.updateQueue);
                this.updateQueue.clear();
            });
            return null;
        } else {
            throw new IllegalArgumentException("unknown method handling: " + method.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getState(String property) {
        return (T) stateMap.get(property);
    }

    public <T> T getState(String property, Class<T> type) {
        if (type.isPrimitive()){
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
