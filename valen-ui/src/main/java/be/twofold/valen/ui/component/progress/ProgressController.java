package be.twofold.valen.ui.component.progress;

import backbonefx.mvvm.*;
import jakarta.inject.*;
import javafx.beans.binding.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

@Singleton
public class ProgressController implements View<VBox, ProgressViewModel> {
    private static final String SPACE = "\u2009";
    private static final String SEPARATOR = SPACE + "/" + SPACE;

    private final ProgressViewModel viewModel = new ProgressViewModel();

    public @FXML VBox root;
    public @FXML ProgressBar progressBar;
    public @FXML Label messageText;
    public @FXML Label percentageText;
    public @FXML Label countText;
    public @FXML Button cancelButton;

    @FXML
    public void initialize() {
        progressBar.progressProperty().bind(viewModel.progressProperty());
        messageText.textProperty().bind(viewModel.messageProperty());
        percentageText.textProperty()
            .bind(viewModel.progressProperty().map(n -> String.format("%.0f%%", n.doubleValue() * 100)));
        countText.textProperty()
            .bind(Bindings.concat(viewModel.workDoneProperty(), SEPARATOR, viewModel.workTotalProperty()));
    }

    @FXML
    public void onCancel(ActionEvent actionEvent) {
        System.out.println("Pressed cancel button");
        viewModel.cancelledProperty().set(true);
    }

    @Override
    public VBox getRoot() {
        return root;
    }

    @Override
    public ProgressViewModel getViewModel() {
        return viewModel;
    }
}
