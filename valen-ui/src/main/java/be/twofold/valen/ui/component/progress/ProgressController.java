package be.twofold.valen.ui.component.progress;

import be.twofold.valen.ui.common.*;
import jakarta.inject.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

@Singleton
public final class ProgressController extends AbstractView<ProgressView.Listener> implements ProgressView {
    private static final String SPACE = "\u2009";
    private static final String SEPARATOR = SPACE + "/" + SPACE;

    private @FXML VBox root;
    private @FXML ProgressBar progressBar;
    private @FXML Label messageText;
    private @FXML Label percentageText;
    private @FXML Label countText;

    @FXML
    public void onCancel() {
        getListener().onCancelClicked();
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
}
