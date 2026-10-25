package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.*;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;

public final class ExportFailuresDialog {
    private static final int MAX_FAILURES = 10;

    private ExportFailuresDialog() {
    }

    static void show(int total, Map<String, List<ExportFailure>> failures) {
        Platform.runLater(() -> {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.getDialogPane().getStylesheets().add(MainWindow.class.getResource("/style.css").toExternalForm());
            alert.setHeaderText("Failed to export " + countFailures(failures) + " of " + total + " assets");
            alert.setContentText(buildSummary(failures));

            var details = new TextArea(buildDetails(failures));
            details.setEditable(false);
            VBox.setVgrow(details, Priority.ALWAYS);

            var logHint = new Label("Full logs: " + AppDirectories.logs());
            logHint.setStyle("-fx-font-size: 0.9em; -fx-text-fill: gray;");

            alert.getDialogPane().setExpandableContent(new VBox(details, logHint));
            alert.getDialogPane().setPrefWidth(750);
            alert.showAndWait();
        });
    }

    private static int countFailures(Map<String, List<ExportFailure>> failures) {
        return failures.values().stream()
            .mapToInt(List::size)
            .sum();
    }

    private static String buildSummary(Map<String, List<ExportFailure>> failures) {
        var builder = new StringBuilder();
        var lines = 0;

        for (var entry : failures.entrySet()) {
            if (lines == MAX_FAILURES) {
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

    private static String buildDetails(Map<String, List<ExportFailure>> failures) {
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
