package be.twofold.valen.ui;

import backbonefx.di.*;
import backbonefx.event.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.*;
import be.twofold.valen.ui.component.main.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
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
    private Session session;

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        Feather feather = Feather.with(
            new EventBusModule(),
            new SettingsModule(),
            new ViewModule()
        );

        feather.instance(EventBus.class)
            .subscribe(MainEvent.class, event -> {
                switch (event) {
                    case MainEvent.GameLoadRequested() -> selectAndLoadGame();
                    case MainEvent.GameSelected(var path) -> tryLoadGame(path);
                    case MainEvent.SaveFileRequested(var filename, var consumer) -> saveFile(filename, consumer);
                }
            });

        presenter = feather.instance(MainPresenter.class);
        settings = feather.instance(Settings.class);
        session = feather.instance(Session.class);

        var icons = Stream.of(16, 24, 32, 48, 64, 96, 128)
            .map(i -> new Image(getClass().getResourceAsStream("/appicon/valen-" + i + ".png")))
            .toList();

        var scene = new Scene(presenter.getFXNode());
        scene.getAccelerators().put(KeyCombination.keyCombination("Ctrl+F"), presenter::focusOnSearch);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        session.gameProperty().subscribe(game -> FxUtils.runOnFxThread(() -> {
            primaryStage.setTitle(game != null ? "Valen :: " + game.entry().name() : "Valen");
        }));
        primaryStage.getIcons().setAll(icons);
        primaryStage.setScene(scene);
        primaryStage.show();

        settings.getGameExecutable()
            .filter(Files::isRegularFile)
            .ifPresent(this::tryLoadGame);
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

    private boolean tryLoadGame(Path path) {
        if (!Files.isRegularFile(path)) {
            showError("Game not found", path + " does not exist.");
            return false;
        }

        var gameFactory = GameFactory.resolve(path);
        if (gameFactory.isEmpty()) {
            showError("Unsupported game", path.getFileName() + " is not a game Valen can open.");
            return false;
        }

        try {
            var newGame = gameFactory.get().load(path);
            var entry = new GameEntry(gameFactory.get().displayName(), path);

            // Swap first, so nothing still points at the old game when it gets closed
            var oldGame = session.getGame();
            session.setGame(new Session.LoadedGame(newGame, entry));
            if (oldGame != null) {
                oldGame.game().close();
            }

            settings.setGameExecutable(path);
            return true;
        } catch (Exception e) {
            FxUtils.showExceptionDialog(e, "Could not load game");
            return false;
        }
    }

    private void showError(String header, String content) {
        var alert = new Alert(Alert.AlertType.ERROR, content);
        alert.setHeaderText(header);
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }
}
