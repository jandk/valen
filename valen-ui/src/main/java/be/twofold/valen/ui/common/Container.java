package be.twofold.valen.ui.common;

import java.util.*;

/**
 * We need so little DI, we'll just roll our own. Just like on my last job hahaha.
 */
public final class Container {
    private final Map<Class<?>, Class<?>> bindings = new IdentityHashMap<>();
    private final Map<Class<?>, Object> instances = new IdentityHashMap<>();
    private final Set<Class<?>> creating = Collections.newSetFromMap(new IdentityHashMap<>());

    public <T> Container bind(Class<T> type, Class<? extends T> impl) {
        bindings.put(type, impl);
        return this;
    }

    public <T> Container instance(Class<T> type, T instance) {
        instances.put(type, instance);
        return this;
    }

    public <T> T get(Class<T> type) {
        var impl = bindings.getOrDefault(type, type);
        var existing = instances.get(impl);
        if (existing != null) {
            return type.cast(existing);
        }
        if (!creating.add(impl)) {
            throw new IllegalStateException("Dependency cycle at " + impl.getName());
        }
        try {
            var constructors = impl.getConstructors();
            if (constructors.length != 1) {
                throw new IllegalStateException(impl.getName() + " needs exactly one public constructor");
            }
            var constructor = constructors[0];
            var args = Arrays.stream(constructor.getParameterTypes())
                .map(this::get)
                .toArray();
            var instance = constructor.newInstance(args);
            instances.put(impl, instance);
            return type.cast(instance);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not create " + impl.getName(), e);
        } finally {
            creating.remove(impl);
        }
    }
}
