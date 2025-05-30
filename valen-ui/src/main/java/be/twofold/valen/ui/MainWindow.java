package be.twofold.valen.ui;

import backbonefx.di.*;
import backbonefx.event.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.*;
import be.twofold.valen.ui.component.main.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.stage.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class MainWindow extends Application {
    private Stage primaryStage;
    private MainPresenter presenter;
    private Settings settings;

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        Feather feather = Feather.with(
            new ControllerModule(),
            new EventBusModule(),
            new SettingsModule(),
            new ViewModule()
        );

        feather.instance(EventBus.class)
            .subscribe(MainEvent.class, event -> {
                switch (event) {
                    case MainEvent.GameLoadRequested() -> selectAndLoadGame();
                    case MainEvent.SaveFileRequested(var filename, var consumer) -> saveFile(filename, consumer);
                }
            });

        presenter = feather.instance(MainPresenter.class);
        settings = feather.instance(Settings.class);

        var icons = Stream.of(16, 24, 32, 48, 64, 96, 128)
            .map(i -> new Image(getClass().getResourceAsStream("/appicon/valen-" + i + ".png")))
            .toList();

        var scene = new Scene(presenter.getFXNode());
        scene.getAccelerators().put(KeyCombination.keyCombination("Ctrl+F"), presenter::focusOnSearch);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setTitle("Valen");
        primaryStage.getIcons().setAll(icons);
        primaryStage.setScene(scene);
        primaryStage.show();

        settings.gameExecutable().get()
            .ifPresentOrElse(
                this::tryLoadGame,
                this::selectAndLoadGame
            );
    }

    private void selectAndLoadGame() {
        Platform.runLater(() -> chooseGame()
            .ifPresent(this::tryLoadGame));
    }

    private void saveFile(String initialFilename, Consumer<Path> consumer) {
        Platform.runLater(() -> {
            var fileChooser = new FileChooser();
            fileChooser.setTitle("Select the output file");
            fileChooser.setInitialFileName(initialFilename);
            var file = fileChooser.showSaveDialog(primaryStage);
            if (file == null) {
                return;
            }

            consumer.accept(file.toPath());
        });
    }

    private Optional<Path> chooseGame() {
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Select the game executable");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Game executable", "*.exe")
        );

        return Optional
            .ofNullable(fileChooser.showOpenDialog(primaryStage))
            .map(File::toPath);
    }

    private void tryLoadGame(Path path) {
        try {
            if (!Files.exists(path)) {
                selectAndLoadGame();
                return;
            }

            var gameFactory = GameFactory.resolve(path);
            if (gameFactory.isEmpty()) {
                selectAndLoadGame();
                return;
            }

            settings.gameExecutable().set(path);
            presenter.setGame(gameFactory.get().load(path));
        } catch (Exception e) {
            FxUtils.showExceptionDialog(e, "Could not load game");
        }
    }
}
