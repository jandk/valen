package be.twofold.valen.ui.component.progress;

import be.twofold.valen.ui.common.*;
import jakarta.inject.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

@Singleton
public final class ProgressViewImpl extends AbstractView<ProgressView.Listener> implements ProgressView {
    private static final String SPACE = "\u2009";
    private static final String SEPARATOR = SPACE + "/" + SPACE;

    private final VBox root = new VBox(10.0);
    private final ProgressBar progressBar = new ProgressBar(0.0);
    private final Label messageText = new Label("Exporting");
    private final Label percentageText = new Label("0%");
    private final Label countText = new Label("0/0");

    @Inject
    public ProgressViewImpl() {
        buildUI();
    }

    @Override
    public Parent getFXNode() {
        return root;
    }

    @Override
    public void updateProgress(Progress progress) {
        progressBar.setProgress(progress.progress());
        messageText.setText(progress.message());
        percentageText.setText((int) (progress.progress() * 100.0 + 0.5) + "%");
        countText.setText((int) progress.workDone() + SEPARATOR + (int) progress.totalWork());
    }

    // region UI

    private void buildUI() {
        root.setPrefWidth(400.0);
        root.setPadding(new Insets(10.0));

        progressBar.setMaxWidth(Double.MAX_VALUE);

        var countRow = new HBox(percentageText, spacer(), countText);

        var cancelButton = new Button("Cancel");
        cancelButton.setOnAction(_ -> getListener().onCancelClicked());
        var cancelRow = new HBox(spacer(), cancelButton, spacer());

        root.getChildren().setAll(progressBar, messageText, countRow, cancelRow);
    }

    private static Pane spacer() {
        var pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        return pane;
    }

    // endregion
}
