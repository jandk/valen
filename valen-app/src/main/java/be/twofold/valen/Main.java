package be.twofold.valen;

import be.twofold.valen.core.util.Platform;
import be.twofold.valen.core.util.logging.*;
import be.twofold.valen.ui.*;
import javafx.application.*;
import org.slf4j.*;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.util.logging.*;

public final class Main {
    static void main(String[] args) throws IOException {
        /*
         * The default texture pool size is only 512MB; some models (like the doom slayer in TDA),
         * have so many textures that we need *a lot* more memory for these, so we bump it to 2GB.
         */
        System.setProperty("prism.maxvram", "2G");

        LogManager.getLogManager().readConfiguration(
            Main.class.getResourceAsStream("/logging.properties")
        );

        Logger log = LoggerFactory.getLogger(Main.class);
        Thread.setDefaultUncaughtExceptionHandler((_, e) -> log.error("Uncaught exception", e));
        logStartup(log, installFileLogging(log));

        Application.launch(MainWindow.class, args);
    }

    private static Path installFileLogging(Logger log) {
        try {
            return FileLogging.install();
        } catch (IOException e) {
            log.warn("Could not open the log file, logging to the console only", e);
            return null;
        }
    }

    private static void logStartup(Logger log, Path logDirectory) {
        log.info("Valen {} starting up", version());
        log.info("  Platform : {} ({})", Platform.current(), System.getProperty("os.name"));
        log.info("  Java     : {} ({})", System.getProperty("java.version"), System.getProperty("java.vendor"));
        log.info("  Max heap : {} MiB", Runtime.getRuntime().maxMemory() >> 20);
        log.info("  Logs     : {}", logDirectory != null ? logDirectory : "(console only)");
    }

    private static String version() {
        var version = Main.class.getPackage().getImplementationVersion();
        return version != null ? version : "(development build)";
    }
}
