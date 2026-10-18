package be.twofold.valen.core.util;

import java.io.*;
import java.util.*;

public final class Throwables {
    private Throwables() {
    }

    public static String getMessage(Throwable throwable) {
        var message = throwable.getMessage();
        return message != null ? message : throwable.getClass().getSimpleName();
    }

    public static Throwable getRootCause(Throwable throwable) {
        var seen = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        var current = throwable;
        while (current.getCause() != null && seen.add(current)) {
            current = current.getCause();
        }
        return current;
    }

    public static String getStackTrace(Throwable throwable) {
        var writer = new StringWriter();
        try (var printWriter = new PrintWriter(writer)) {
            throwable.printStackTrace(printWriter);
        }
        return writer.toString();
    }
}
