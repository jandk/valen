package be.twofold.valen.ui.component;

import be.twofold.valen.ui.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.*;

public final class FxUtils {

    public static void showExceptionDialog(Exception exception, String text) {
        var alert = new Alert(Alert.AlertType.ERROR, "An error occurred");
        alert.getDialogPane().getStylesheets().add(MainWindow.class.getResource("/style.css").toExternalForm());
        alert.setHeaderText(text);
        alert.setContentText(exception.getMessage());

        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        exception.printStackTrace(pw);

        var label = new Label("This is the stacktrace");

        var textArea = new TextArea(sw.toString());
        textArea.setEditable(false);
        textArea.setWrapText(false);

        VBox.setVgrow(textArea, Priority.ALWAYS);

        var content = new VBox();
        content.getChildren().add(label);
        content.getChildren().add(textArea);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(content);

        alert.showAndWait();
    }
}
