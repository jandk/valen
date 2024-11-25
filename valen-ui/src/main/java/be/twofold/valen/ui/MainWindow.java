package be.twofold.valen.ui;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.settings.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.stage.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MainWindow extends Application {
    @Override
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

        var path = SettingsManager.get().getGameExecutable().get();
        var game = resolveGameFactory(path).load(path);
        var archive = game.loadArchive("common");

//        var manager = DaggerManagerFactory.create().fileManager();
//        manager.load(SettingsManager.get().getGameDirectory().get().resolve("base"));
//        try {
//            manager.select("common");
//        } catch (IOException e) {
//            System.out.println("Failed to select common");
//            throw new UncheckedIOException(e);
//        }

        var presenter = DaggerPresenterFactory.create().presenter();
        presenter.setArchive(archive);
        var scene = new Scene(presenter.getView().getView());
//        System.out.println("Mnemonics:");
//        scene.getMnemonics().forEach((key, value) -> System.out.println(key + " -> " + value));
//        System.out.println("Accelerators:");
//        scene.getAccelerators().forEach((key, value) -> System.out.println(key + " -> " + value));
//        scene.getAccelerators().put(KeyCombination.valueOf("Ctrl+P"), window::togglePreview);

        var icons = List.of(16, 24, 32, 48, 64, 96, 128).stream()
            .map(i -> new Image(getClass().getResourceAsStream("/appicon/valen-" + i + ".png")))
            .toList();

        primaryStage.setTitle("Valen");
        primaryStage.getIcons().setAll(icons);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static GameFactory<?> resolveGameFactory(Path path) {
        return ServiceLoader.load(GameFactory.class).stream()
            .map(ServiceLoader.Provider::get)
            .filter(factory -> factory.canLoad(path))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No GameFactory found for " + path));
    }
}
