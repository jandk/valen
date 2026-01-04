package be.twofold.valen;

import be.twofold.valen.ui.*;
import javafx.application.*;
import org.slf4j.*;

import java.io.*;
import java.util.logging.*;

public final class Main {
    static void main(String[] args) throws IOException {
        LogManager.getLogManager().readConfiguration(
            Main.class.getResourceAsStream("/logging.properties")
        );

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            LoggerFactory.getLogger(Main.class).error("Uncaught exception", e);
        });

        Application.launch(MainWindow.class, args);
    }
}
