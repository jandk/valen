package be.twofold.valen.ui;

import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.*;
import be.twofold.valen.ui.component.audioviewer.*;
import be.twofold.valen.ui.component.filelist.*;
import be.twofold.valen.ui.component.main.*;
import be.twofold.valen.ui.component.metaview.*;
import be.twofold.valen.ui.component.modelviewer.*;
import be.twofold.valen.ui.component.preview.*;
import be.twofold.valen.ui.component.progress.*;
import be.twofold.valen.ui.component.rawview.*;
import be.twofold.valen.ui.component.settings.*;
import be.twofold.valen.ui.component.textureviewer.*;
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
        var container = new Container()
            .instance(Dialogs.class, new FxDialogs(primaryStage))
            .instance(Settings.class, SettingsManager.get())
            .instance(Stage.class, primaryStage)
            .instance(UiThread.class, FX::run)
            .bind(AudioView.class, AudioViewImpl.class)
            .bind(FileListView.class, FileListViewImpl.class)
            .bind(MainView.class, MainViewImpl.class)
            .bind(MetaView.class, MetaViewImpl.class)
            .bind(ModelView.class, ModelViewImpl.class)
            .bind(PreviewView.class, PreviewViewImpl.class)
            .bind(ProgressView.class, ProgressViewImpl.class)
            .bind(RawView.class, RawViewImpl.class)
            .bind(SettingsView.class, SettingsViewImpl.class)
            .bind(TextureView.class, TextureViewImpl.class);

        var session = container.get(Session.class);
        var gameService = container.get(GameService.class);
        var mainPresenter = container.get(MainPresenter.class);
        container.get(ProgressPresenter.class); // Just need instantiating

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

        // Games
        gameService.start();
    }
}
