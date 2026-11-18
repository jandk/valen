package be.twofold.valen.ui.component;

import be.twofold.valen.ui.component.main.*;
import be.twofold.valen.ui.service.*;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public final class FxDialogs implements Dialogs {
    private final Stage stage;

    public FxDialogs(Stage stage) {
        this.stage = stage;
    }

    @Override
    public CompletableFuture<Optional<Path>> chooseGameExecutable() {
        return FxUtils.runOnFxThread(() -> {
            var chooser = new FileChooser();
            chooser.setTitle("Select the game executable");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game executable", "*.exe"));
            return Optional.ofNullable(chooser.showOpenDialog(stage)).map(File::toPath);
        });
    }

    @Override
    public CompletableFuture<Optional<Path>> chooseSaveFile(String initialFilename) {
        return FxUtils.runOnFxThread(() -> {
            var fileChooser = new FileChooser();
            fileChooser.setTitle("Select the output file");
            fileChooser.setInitialFileName(initialFilename);
            return Optional.ofNullable(fileChooser.showSaveDialog(stage)).map(File::toPath);
        });
    }

    @Override
    public void showError(String header, String message) {
        FxUtils.runOnFxThread(() -> {
            var alert = new Alert(Alert.AlertType.ERROR, message);
            alert.setHeaderText(header);
            alert.initOwner(stage);
            alert.show();
        });
    }

    @Override
    public void showException(String message, Throwable throwable) {
        FxUtils.showExceptionDialog(throwable, message);
    }

    @Override
    public void showExportFailures(int total, Map<String, List<ExportFailure>> failures) {
        ExportFailuresDialog.show(total, failures);
    }
}
