package be.twofold.valen.ui.component.progress;

import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.*;
import be.twofold.valen.ui.service.*;
import jakarta.inject.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

@Singleton
public final class ProgressViewImpl extends AbstractView<ProgressView.Listener> implements ProgressView {
    private final Dialog<Void> dialog = new Dialog<>();
    private final VBox root = new VBox(10.0);
    private final ProgressBar progressBar = new ProgressBar(0.0);
    private final Label messageText = new Label("Exporting");
    private final Label percentageText = new Label("0%");
    private final Label countText = new Label("0/0");

    private boolean closing;

    @Inject
    public ProgressViewImpl(Stage owner) {
        buildUI();
        dialog.initOwner(owner);
        dialog.setTitle("Exporting...");
        dialog.getDialogPane().setContent(root);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        dialog.setOnCloseRequest(event -> {
            if (closing) {
                return;
            }
            event.consume();
            dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setDisable(true);
            messageText.setText("Cancelling...");
            getListener().onCancelClicked();
        });
    }

    @Override
    public Parent getFXNode() {
        return root;
    }

    @Override
    public void updateProgress(ExportProgress progress) {
        FxUtils.runOnFxThread(() -> {
            var fraction = (double) progress.done() / progress.total();
            progressBar.setProgress(fraction);
            messageText.setText(progress.message());
            percentageText.setText((int) (fraction * 100.0 + 0.5) + "%");
            countText.setText(progress.done() + Constants.TS_SLASH + progress.total());
        });
    }

    @Override
    public void show() {
        FxUtils.runOnFxThread(() -> {
            closing = false;
            dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setDisable(false);
            dialog.show();
        });
    }

    @Override
    public void hide() {
        FxUtils.runOnFxThread(() -> {
            closing = true;
            dialog.close();
        });
    }

    // region UI

    private void buildUI() {
        root.setPrefWidth(400.0);
        root.setPadding(new Insets(10.0));

        progressBar.setMaxWidth(Double.MAX_VALUE);

        var countRow = new HBox(percentageText, spacer(), countText);

        root.getChildren().setAll(progressBar, messageText, countRow);
    }

    private static Pane spacer() {
        var pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        return pane;
    }

    // endregion
}
