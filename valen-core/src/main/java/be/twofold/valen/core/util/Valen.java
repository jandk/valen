package be.twofold.valen.core.util;

import java.io.*;
import java.util.*;

public final class Valen {
    private static final String DEFAULT_VERSION = "dev";
    private static final String VERSION = loadVersion();

    private Valen() {
    }

    private static String loadVersion() {
        try (var in = Valen.class.getResourceAsStream("valen.properties")) {
            if (in == null) {
                return DEFAULT_VERSION;
            }

            var properties = new Properties();
            properties.load(in);
            return properties.getProperty("version", DEFAULT_VERSION);
        } catch (IOException e) {
            return DEFAULT_VERSION;
        }
    }

    public static String version() {
        return VERSION;
    }
}
