package be.twofold.valen.ui;

import be.twofold.valen.reader.*;
import be.twofold.valen.ui.settings.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;

import java.io.*;
import java.nio.file.*;

public class MainWindow extends Application {
    @Override
    public void start(Stage primaryStage) {
        if (SettingsManager.get().getGameDirectory().isEmpty()) {
            var fileChooser = new FileChooser();
            fileChooser.setTitle("Select the game executable");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Game executable", "DoomEternalx64vk.exe")
            );
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                Path path = selectedFile.toPath();
                SettingsManager.get().setGameDirectory(path.getParent());
            }
        }

        var manager = DaggerManagerFactory.create().fileManager();
        manager.load(SettingsManager.get().getGameDirectory().get().resolve("base"));
        try {
            manager.select("common");
        } catch (IOException e) {
            System.out.println("Failed to select common");
            throw new UncheckedIOException(e);
        }

        var presenter = DaggerPresenterFactory.create().presenter();
        presenter.setFileManager(manager);
        Scene scene = new Scene(presenter.getView().getView());
//        System.out.println("Mnemonics:");
//        scene.getMnemonics().forEach((key, value) -> System.out.println(key + " -> " + value));
//        System.out.println("Accelerators:");
//        scene.getAccelerators().forEach((key, value) -> System.out.println(key + " -> " + value));
//        scene.getAccelerators().put(KeyCombination.valueOf("Ctrl+P"), window::togglePreview);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
