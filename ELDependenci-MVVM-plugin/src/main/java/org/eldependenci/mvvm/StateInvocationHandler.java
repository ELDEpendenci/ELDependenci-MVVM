package org.eldependenci.mvvm;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class StateInvocationHandler implements InvocationHandler {


    private final Consumer<String> onPropertyUpdate;

    private final Map<String, Object> stateMap = new ConcurrentHashMap<>();

    public StateInvocationHandler(Consumer<String> onPropertyUpdate) {
        this.onPropertyUpdate = onPropertyUpdate;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().startsWith("set")) {
            var property = method.getName().substring(3).toLowerCase();
            if (args.length != 1) throw new IllegalArgumentException("setter method must have exactly one argument.");
            this.stateMap.put(property, args[0]);
            this.onPropertyUpdate.accept(property);
            return null;
        } else if (method.getName().startsWith("get")) {
            var property = method.getName().substring(3).toLowerCase();
            if (args.length != 0) throw new IllegalArgumentException("getter method must not have any arguments");
            return method.getReturnType().cast(this.stateMap.get(property));
        } else {
            throw new IllegalArgumentException("unknown method handling: " + method.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getState(String property) {
        return (T) stateMap.get(property);
    }

    public <T> T getState(String property, Class<T> type) {
        return type.cast(stateMap.get(property));
    }
}
