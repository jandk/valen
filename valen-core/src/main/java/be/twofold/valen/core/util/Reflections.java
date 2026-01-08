package be.twofold.valen.core.util;

import java.lang.reflect.*;
import java.util.*;

public final class Reflections {
    private Reflections() {
    }

    public static Optional<ParameterizedType> getParameterizedType(Class<?> clazz, Class<?> parameterizedType) {
        if (clazz == null) {
            return Optional.empty();
        }

        // Step 1: Check immediate supertypes
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType pt) {
            if (pt.getRawType().equals(parameterizedType)) {
                return Optional.of(pt);
            }
        }
        for (Type genericInterface : clazz.getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType pt) {
                if (pt.getRawType().equals(parameterizedType)) {
                    return Optional.of(pt);
                }
            }
        }

        // Step 2: Check super classes
        if (genericSuperclass instanceof ParameterizedType pt) {
            Class<?> superClass = (Class<?>) pt.getRawType();
            Optional<ParameterizedType> result = getParameterizedType(superClass, parameterizedType);
            if (result.isPresent()) {
                return result;
            }
        } else if (genericSuperclass instanceof Class) {
            Optional<ParameterizedType> result = getParameterizedType((Class<?>) genericSuperclass, parameterizedType);
            if (result.isPresent()) {
                return result;
            }
        }

        // Step 3: Check super interfaces
        for (Type genericInterface : clazz.getGenericInterfaces()) {
            Class<?> interfaceClass;
            if (genericInterface instanceof ParameterizedType) {
                interfaceClass = (Class<?>) ((ParameterizedType) genericInterface).getRawType();
            } else {
                interfaceClass = (Class<?>) genericInterface;
            }
            Optional<ParameterizedType> result = getParameterizedType(interfaceClass, parameterizedType);
            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }
}
