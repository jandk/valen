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
            case "linux" -> OperatingSystem.Linux;
            case "windows" -> OperatingSystem.Windows;
            case "mac" -> OperatingSystem.Mac;
            default -> throw new RuntimeException("Unsupported OS: " + os);
        };
    }

    public static OperatingSystem current() {
        return CURRENT;
    }
}
