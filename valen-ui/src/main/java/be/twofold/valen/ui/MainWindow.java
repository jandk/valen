package be.twofold.valen.ui;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.settings.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.stage.*;

import java.io.*;
import java.util.stream.*;

public class MainWindow extends Application {
    @Override
    @SuppressWarnings("DataFlowIssue")
    public void start(Stage primaryStage) throws IOException {
        if (SettingsManager.get().getGameExecutable().isEmpty()) {
            var fileChooser = new FileChooser();
            fileChooser.setTitle("Select the game executable");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Game executable", "*.exe")
            );
            var selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                SettingsManager.get().setGameExecutable(selectedFile.toPath());
            }
        }

        var presenter = DaggerPresenterFactory.create().presenter();
        var scene = new Scene(presenter.getView().getView());
//        System.out.println("Mnemonics:");
//        scene.getMnemonics().forEach((key, value) -> System.out.println(key + " -> " + value));
//        System.out.println("Accelerators:");
//        scene.getAccelerators().forEach((key, value) -> System.out.println(key + " -> " + value));
//        scene.getAccelerators().put(KeyCombination.valueOf("Ctrl+P"), window::togglePreview);

        var icons = Stream.of(16, 24, 32, 48, 64, 96, 128)
            .map(i -> new Image(getClass().getResourceAsStream("/appicon/valen-" + i + ".png")))
            .toList();

        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setTitle("Valen");
        primaryStage.getIcons().setAll(icons);
        primaryStage.setScene(scene);
        primaryStage.show();

        var path = SettingsManager.get().getGameExecutable().get();
        var game = GameFactory.resolve(path).load(path);
        presenter.setGame(game);
    }
}
