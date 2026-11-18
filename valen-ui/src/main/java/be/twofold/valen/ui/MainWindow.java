package be.twofold.valen.ui;

import backbonefx.di.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.*;
import be.twofold.valen.ui.component.main.*;
import be.twofold.valen.ui.component.progress.*;
import be.twofold.valen.ui.service.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.stage.*;

import java.util.stream.*;

public final class MainWindow extends Application {
    @Override
    @SuppressWarnings("DataFlowIssue")
    public void start(Stage primaryStage) {
        // Wiring
        var feather = Feather.with(
            new EventBusModule(),
            new SettingsModule(),
            new ViewModule(),
            new WindowModule(primaryStage)
        );
        var session = feather.instance(Session.class);
        var gameService = feather.instance(GameService.class);
        var mainPresenter = feather.instance(MainPresenter.class);
        // Not referenced here: it subscribes to ExportService and shows its own window during exports
        feather.instance(ProgressPresenter.class);

        // Window
        var scene = new Scene(mainPresenter.getFXNode());
        scene.getAccelerators().put(KeyCombination.keyCombination("Ctrl+F"), mainPresenter::focusOnSearch);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        var icons = Stream.of(16, 24, 32, 48, 64, 96, 128)
            .map(i -> new Image(getClass().getResourceAsStream("/appicon/valen-" + i + ".png")))
            .toList();
        primaryStage.getIcons().setAll(icons);

        session.gameProperty().subscribe(game -> FX.run(() -> {
            primaryStage.setTitle(game != null ? "Valen :: " + game.entry().name() : "Valen");
        }));
        primaryStage.setScene(scene);
        primaryStage.show();

        // Go: loads the saved game and scans Steam, after everything is subscribed
        gameService.start();
    }
}
