package be.twofold.valen.core.util;

import java.util.*;

public final class Throwables {
    private Throwables() {
    }

    public static Throwable rootCause(Throwable throwable) {
        var seen = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        var current = throwable;
        while (current.getCause() != null && seen.add(current)) {
            current = current.getCause();
        }
        return current;
    }
}
