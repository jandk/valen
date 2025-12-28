package be.twofold.valen.ui.component;

import backbonefx.di.*;
import jakarta.inject.*;
import javafx.fxml.*;
import javafx.scene.*;
import org.slf4j.*;

import java.io.*;

public final class ViewLoader {
    private static final Logger LOG = LoggerFactory.getLogger(ViewLoader.class);

    private final FXMLLoader fxmlLoader = new FXMLLoader();

    @Inject
    public ViewLoader(Feather feather) {
        fxmlLoader.setControllerFactory(feather::instance);
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
