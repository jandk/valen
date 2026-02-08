package be.twofold.valen.ui.component;

import backbonefx.di.*;
import be.twofold.valen.ui.common.*;
import jakarta.inject.*;
import javafx.fxml.*;

import java.io.*;

public class ViewLoader {
    private final Feather feather;

    @Inject
    public ViewLoader(Feather feather) {
        this.feather = feather;
    }

    public <P> P loadPresenter(Class<P> presenterClass) {
        return feather.instance(presenterClass);
    }

    public <V extends View<?>, P extends AbstractPresenter<V>> P loadPresenter(Class<P> presenterClass, String fxmlPath) {
        try {
            // Load FXML with Feather as controller factory
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(feather::instance);
            loader.load();

            // Get the view (was created by FXML loader via feather)
            V view = loader.getController();

            // Create the presenter (feather injects dependencies including the view)
            P presenter = feather.instance(presenterClass);

            return presenter;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
