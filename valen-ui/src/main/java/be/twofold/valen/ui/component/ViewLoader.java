package be.twofold.valen.ui.component;

import jakarta.inject.*;
import javafx.fxml.*;
import javafx.scene.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;

public enum ViewLoader {
    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(ViewLoader.class);

    private final Map<Class<?>, Provider<Controller>> controllerProviders
        = DaggerControllerFactory.create().controllers();
    private final FXMLLoader fxmlLoader = new FXMLLoader();

    ViewLoader() {
        fxmlLoader.setControllerFactory(type -> controllerProviders.get(type).get());
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
