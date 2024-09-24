package be.twofold.valen.ui;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.settings.*;
import javafx.application.*;
import javafx.scene.*;
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

        // var path = SettingsManager.get().getGameExecutable().get();
        // TODO: Don't hardcode this
        var path = Path.of("D:\\SteamLibrary\\steamapps\\common\\Space Marine 2\\Warhammer 40000 Space Marine 2.exe");
//        var path = SettingsManager.get().getGameDirectory().get().resolve("DOOMEternalx64vk.exe");
        var game = resolveGameFactory(path).load(path);
        var archive = game.loadArchive("client_pc");
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
