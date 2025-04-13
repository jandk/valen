package be.twofold.valen.core.util;

import java.util.*;

public enum OperatingSystem {
    Linux,
    Windows,
    Mac;

    private static final OperatingSystem CURRENT;

    static {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        CURRENT = switch (os.split(" ")[0]) {
            case "linux" -> Linux;
            case "windows" -> Windows;
            case "mac" -> Mac;
            default -> throw new ExceptionInInitializerError("Unsupported OS: " + os);
        };
    }

    public static OperatingSystem current() {
        return CURRENT;
    }
}
