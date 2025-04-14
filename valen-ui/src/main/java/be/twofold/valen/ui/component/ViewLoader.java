package be.twofold.valen.ui.component;

import backbonefx.di.*;
import javafx.fxml.*;
import javafx.scene.*;
import org.slf4j.*;

import java.io.*;

public enum ViewLoader {
    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(ViewLoader.class);

    private final FXMLLoader fxmlLoader = new FXMLLoader();

    ViewLoader() {
        fxmlLoader.setControllerFactory(Feather.with()::instance);
    }

    public Parent load(String fxml) {
        LOG.info("Loading FXML {}", fxml);
        try (var in = getClass().getResourceAsStream(fxml)) {
            return fxmlLoader.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
