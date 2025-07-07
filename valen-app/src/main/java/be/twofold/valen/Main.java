package be.twofold.valen;

import be.twofold.valen.ui.*;
import javafx.application.*;

import java.io.*;
import java.util.logging.*;

public final class Main {
    public static void main(String[] args) throws IOException {
        LogManager.getLogManager().readConfiguration(
            Main.class.getResourceAsStream("/logging.properties")
        );

        Application.launch(MainWindow.class, args);
    }
}
