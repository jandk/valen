package be.twofold.valen.ui.component;

import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.*;
import javafx.application.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.util.concurrent.*;
import java.util.function.*;

public final class FxUtils {

    /**
     * Runs the given action on the JavaFX Application Thread.
     */
    public static void runOnFxThread(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }

    public static <T> CompletableFuture<T> runOnFxThread(Supplier<T> action) {
        var future = new CompletableFuture<T>();
        runOnFxThread(() -> {
            try {
                future.complete(action.get());
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    /**
     * Wraps a {@link DecodedImage} in a JavaFX image without copying: the image
     * reads directly from the decoded buffer.
     */
    public static WritableImage toWritableImage(DecodedImage image) {
        return new WritableImage(new PixelBuffer<>(
            image.width(),
            image.height(),
            image.pixels().asMutableBuffer(),
            PixelFormat.getByteBgraPreInstance()
        ));
    }

    public static void showExceptionDialog(Throwable throwable, String text) {
        runOnFxThread(() -> {
            var alert = new Alert(Alert.AlertType.ERROR, "An error occurred");
            alert.getDialogPane().getStylesheets().add(MainWindow.class.getResource("/style.css").toExternalForm());
            alert.setHeaderText(text);
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
}
