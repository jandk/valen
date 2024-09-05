package be.twofold.valen.ui.util;

import java.lang.reflect.*;
import java.util.*;

public final class ListenerHelper<T> {
    private final Set<T> listeners = Collections.synchronizedSet(new HashSet<>());

    private final T proxy;
    private final Class<T> listenerClass;

    public ListenerHelper(Class<T> listenerClass) {
        this.proxy = buildProxy(listenerClass);
        this.listenerClass = listenerClass;
    }

    public void addListener(T listener) {
        listeners.add(listener);
    }

    public void removeListener(T listener) {
        listeners.remove(listener);
    }

    public T fire() {
        return proxy;
    }

    @SuppressWarnings("unchecked")
    private T buildProxy(Class<T> listenerClass) {
        return (T) Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class[]{listenerClass},
            (proxy, method, args) -> switch (method.getName()) {
                case "equals" -> super.equals(args[0]);
                case "hashCode" -> super.hashCode();
                case "toString" -> super.toString();
                default -> {
                    for (T listener : listeners) {
                        method.invoke(listener, args);
                    }
                    yield null;
                }
            }
        );
    }

    @Override
    public String toString() {
        return listenerClass.getSimpleName() + "(" + listeners.size() + " listeners)";
    }
}
