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
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        presenter = DaggerPresenterFactory.create().presenter();
        var scene = new Scene(presenter.getView().getView());
        scene.getAccelerators().put(KeyCombination.keyCombination("Ctrl+F"), presenter::focusOnSearch);

        var icons = Stream.of(16, 24, 32, 48, 64, 96, 128)
            .map(i -> new Image(getClass().getResourceAsStream("/appicon/valen-" + i + ".png")))
            .toList();

        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setTitle("Valen");
        primaryStage.getIcons().setAll(icons);
        primaryStage.setScene(scene);
        primaryStage.show();

        if (settings.getGameExecutable().isEmpty()) {
            chooseGame();
        }
        loadGame();
    }

    private void chooseGame() {
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Select the game executable");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Game executable", "*.exe")
        );
        var selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }

        var selectedPath = selectedFile.toPath();
        if (GameFactory.resolve(selectedPath).isPresent()) {
            settings.setGameExecutable(selectedPath);
        }
    }

    private void loadGame() throws IOException {
        if (settings.getGameExecutable().isEmpty()) {
            return;
        }
        var path = settings.getGameExecutable().orElseThrow();
        var game = GameFactory.resolve(path).orElseThrow().load(path);
        presenter.setGame(game);
    }

}
