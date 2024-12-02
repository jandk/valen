package be.twofold.valen.ui;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.settings.*;
import be.twofold.valen.ui.window.*;
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
    private final Settings settings;
    private Stage primaryStage;
    private MainPresenter presenter;

    public MainWindow() {
        this(SettingsManager.get());
    }

    MainWindow(Settings settings) {
        this.settings = settings;
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        var factory = DaggerMainFactory.create();

        factory.eventBus()
            .receiverFor(MainEvent.class)
            .consume(event -> {
                switch (event) {
                    case MainEvent.GameLoadRequested() -> selectAndLoadGame();
                    case MainEvent.SaveFileRequested(var filename, var consumer) -> saveFile(filename, consumer);
                }
            });

        presenter = factory.presenter();

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

        if (settings.getGameExecutable().isPresent()) {
            loadGame(settings.getGameExecutable().get());
        } else {
            selectAndLoadGame();
        }
    }

    private void selectAndLoadGame() {
        Platform.runLater(() -> chooseGame()
            .ifPresent(path -> {
                settings.setGameExecutable(path);
                loadGame(path);
            }));
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

        return Optional.ofNullable(fileChooser.showOpenDialog(primaryStage))
            .map(File::toPath)
            .filter(path -> GameFactory.resolve(path).isPresent());
    }

    private void loadGame(Path path) {
        try {
            var game = GameFactory.resolve(path).orElseThrow().load(path);
            presenter.setGame(game);
        } catch (IOException e) {
            System.err.println("Could not load game " + path);
            e.printStackTrace(System.err);
        }
    }
}
