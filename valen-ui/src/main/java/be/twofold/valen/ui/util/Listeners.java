package be.twofold.valen.ui.util;

import java.lang.reflect.*;
import java.util.*;

public final class Listeners<T> {
    private final Set<T> listeners = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

    private final T proxy;
    private final Class<T> listenerClass;

    public Listeners(Class<T> listenerClass) {
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
                        try {
                            method.invoke(listener, args);
                        } catch (Exception e) {
                            System.err.println("Failed to invoke listener: " + e.getMessage());
                        }
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
