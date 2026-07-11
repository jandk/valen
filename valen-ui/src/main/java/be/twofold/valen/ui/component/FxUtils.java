package be.twofold.valen.ui.component;

import be.twofold.valen.ui.*;
import javafx.application.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.io.*;

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
        Platform.runLater(() -> {
            var alert = new Alert(Alert.AlertType.ERROR, "An error occurred");
            alert.getDialogPane().getStylesheets().add(MainWindow.class.getResource("/style.css").toExternalForm());
            alert.setHeaderText(text);
            alert.setContentText(throwable.getMessage());

            var sw = new StringWriter();
            var pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);

            var label = new Label("This is the stacktrace");

            var textArea = new TextArea(sw.toString());
            textArea.setEditable(false);
            textArea.setWrapText(false);

            VBox.setVgrow(textArea, Priority.ALWAYS);

            var content = new VBox();
            content.getChildren().add(label);
            content.getChildren().add(textArea);

            // Set expandable stacktrace into the dialog pane.
            alert.getDialogPane().setExpandableContent(content);
            alert.showAndWait();
        });
    }
}
