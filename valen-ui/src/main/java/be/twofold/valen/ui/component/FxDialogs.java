package be.twofold.valen.ui.component;

import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.*;
import be.twofold.valen.ui.service.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
        return FX.supply(() -> {
            var chooser = new FileChooser();
            chooser.setTitle("Select the game executable");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game executable", "*.exe"));
            return Optional.ofNullable(chooser.showOpenDialog(stage)).map(File::toPath);
        });
    }

    @Override
    public CompletableFuture<Optional<Path>> chooseSaveFile(String initialFilename) {
        return FX.supply(() -> {
            var fileChooser = new FileChooser();
            fileChooser.setTitle("Select the output file");
            fileChooser.setInitialFileName(initialFilename);
            return Optional.ofNullable(fileChooser.showSaveDialog(stage)).map(File::toPath);
        });
    }

    @Override
    public void showError(String header, String message) {
        FX.run(() -> {
            var alert = new Alert(Alert.AlertType.ERROR, message);
            alert.setHeaderText(header);
            alert.initOwner(stage);
            alert.show();
        });
    }

    @Override
    public void showException(String message, Throwable throwable) {
        FX.run(() -> {
            var alert = new Alert(Alert.AlertType.ERROR, "An error occurred");
            alert.getDialogPane().getStylesheets().add(MainWindow.class.getResource("/style.css").toExternalForm());
            alert.setHeaderText(message);
            alert.setContentText(Throwables.getMessage(throwable));

            var label = new Label("This is the stacktrace");

            var textArea = new TextArea(Throwables.getStackTrace(throwable));
            textArea.setEditable(false);
            textArea.setWrapText(false);

            VBox.setVgrow(textArea, Priority.ALWAYS);

            // Show log directory
            var logHint = new Label("Full logs: " + AppDirectories.logs());
            logHint.setStyle("-fx-font-size: 0.9em; -fx-text-fill: gray;");

            var content = new VBox();
            content.getChildren().add(label);
            content.getChildren().add(textArea);
            content.getChildren().add(logHint);

            // Set expandable stacktrace into the dialog pane.
            alert.getDialogPane().setExpandableContent(content);
            alert.showAndWait();
        });
    }

    @Override
    public void showExportFailures(int total, Map<String, List<ExportFailure>> failures) {
        FX.run(() -> {
            int failureCount = failures.values().stream()
                .mapToInt(List::size)
                .sum();

            var alert = new Alert(Alert.AlertType.WARNING);
            alert.getDialogPane().getStylesheets().add(MainWindow.class.getResource("/style.css").toExternalForm());
            alert.setHeaderText("Failed to export " + failureCount + " of " + total + " assets");
            alert.setContentText(buildFailureSummary(failures));

            var details = new TextArea(buildFailureDetails(failures));
            details.setEditable(false);
            VBox.setVgrow(details, Priority.ALWAYS);

            var logHint = new Label("Full logs: " + AppDirectories.logs());
            logHint.setStyle("-fx-font-size: 0.9em; -fx-text-fill: gray;");

            alert.getDialogPane().setExpandableContent(new VBox(details, logHint));
            alert.getDialogPane().setPrefWidth(750);
            alert.showAndWait();
        });
    }

    private static String buildFailureSummary(Map<String, List<ExportFailure>> failures) {
        var builder = new StringBuilder();
        var lines = 0;

        for (var entry : failures.entrySet()) {
            if (lines == 10) {
                builder.append("...and ").append(failures.size() - lines).append(" more");
                break;
            }
            var group = entry.getValue();
            builder.append(entry.getKey()).append(" (").append(group.size()).append(")\n")
                .append("    ").append(group.getFirst().message()).append("\n");
            lines++;
        }

        return builder.substring(0, builder.length() - 1);
    }

    private static String buildFailureDetails(Map<String, List<ExportFailure>> failures) {
        var builder = new StringBuilder();

        for (var entry : failures.entrySet()) {
            builder.append(entry.getKey()).append("\n");
            for (ExportFailure failure : entry.getValue()) {
                builder.append("    ").append(failure.id().fullName())
                    .append(": ").append(failure.message())
                    .append("\n");
            }
            builder.append("\n");
        }

        return builder.substring(0, builder.length() - 1);
    }
}
