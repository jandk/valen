package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.*;
import be.twofold.valen.ui.component.preview.*;
import be.twofold.valen.ui.component.settings.*;
import jakarta.inject.*;
import javafx.animation.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.slf4j.*;

import java.util.*;

@Singleton
public final class MainViewImpl extends AbstractView<MainView.Listener> implements MainView {
    private static final Logger log = LoggerFactory.getLogger(MainViewImpl.class);

    private final BorderPane view = new BorderPane();
    private final SplitPane splitPane = new SplitPane();

    private final ToggleButton previewButton = new ToggleButton("Preview");
    private final ToggleButton settingsButton = new ToggleButton("Settings");

    private final ComboBox<String> archiveChooser = new ComboBox<>();
    private final TextField searchTextField = new TextField();

    private final PreviewTabPane tabPane;
    private final SettingsPresenter settingsPresenter;

    private boolean suppressToggleEvents;

    @Inject
    MainViewImpl(PreviewTabPane tabPane, ViewLoader viewLoader) {
        this.tabPane = tabPane;
        this.settingsPresenter = viewLoader.loadPresenter(SettingsPresenter.class, "/fxml/Settings.fxml");

        buildUI();
    }

    @Override
    public void setFileListView(Node node) {
        if (node == null) {
            log.error("setFileListView called with null node");
            return;
        }
        splitPane.getItems().setAll(node);
    }

    @Override
    public Parent getFXNode() {
        return view;
    }

    @Override
    public boolean isSidePaneVisible() {
        return splitPane.getItems().size() == 2;
    }

    @Override
    public void setArchives(List<String> archives) {
        archiveChooser.getItems().setAll(archives);
        // archiveChooser.getSelectionModel().select(0);
    }

    @Override
    public void setupPreview(Asset asset, Object assetData) {
        tabPane.setData(asset.type(), assetData);
    }

    @Override
    public void focusOnSearch() {
        searchTextField.requestFocus();
    }

    @Override
    public void setExporting(boolean exporting) {
        Platform.runLater(() -> {
            log.info("Exporting: {} at {}", exporting, System.currentTimeMillis());
            view.setDisable(exporting);
        });
    }

    @Override
    public void showPreview(boolean enabled) {
        setSidePanelEnabled(enabled, tabPane);
        setToggleSelected(previewButton, enabled);
    }

    @Override
    public void showSettings(boolean enabled) {
        setSidePanelEnabled(enabled, settingsPresenter.getView().getFXNode());
        setToggleSelected(settingsButton, enabled);
    }

    private void selectArchive(String archiveName) {
        if (archiveName == null) {
            return;
        }
        getListener().onArchiveSelected(archiveName);
    }

    private void setSidePanelEnabled(boolean enabled, Node node) {
        if (node == null) {
            log.error("setSidePanelEnabled called with null node");
            return;
        }
        if (enabled) {
            if (isSidePaneVisible()) {
                return;
            }
            splitPane.getItems().add(node);
            splitPane.setDividerPositions(0.60);
        } else {
            if (!isSidePaneVisible()) {
                return;
            }
            splitPane.getItems().remove(1);
            splitPane.setDividerPositions();
        }
    }

    private void setToggleSelected(ToggleButton button, boolean selected) {
        suppressToggleEvents = true;
        try {
            button.setSelected(selected);
        } finally {
            suppressToggleEvents = false;
        }
    }

    // region UI

    private void buildUI() {
        view.setPrefSize(1250, 666);
        view.setTop(buildToolBar());
        view.setCenter(buildMainContent());
        view.setBottom(buildStatusBar());
    }

    private SplitPane buildMainContent() {
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        return splitPane;
    }

    private HBox buildStatusBar() {
        var pause = new PauseTransition();
        searchTextField.setId("searchTextField");
        searchTextField.setPromptText("Search");
        searchTextField.textProperty().addListener((_, _, newValue) -> {
            pause.setOnFinished(_ -> getListener().onSearchChanged(newValue));
            pause.playFromStart();
        });
        searchTextField.setMinWidth(160);
        searchTextField.setPrefWidth(160);

        var searchClearButton = new Button("Clear");
        searchClearButton.setDisable(true);
        searchClearButton.setOnAction(_ -> searchTextField.setText(""));
        searchClearButton.disableProperty().bind(searchTextField.textProperty().isEmpty());
        searchClearButton.setMinWidth(searchClearButton.getPrefWidth());

        var pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);

        var hBox = new HBox(
            searchTextField, searchClearButton,
            pane
        );
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(5.0);
        hBox.setPadding(new Insets(3.0));
        return hBox;
    }

    private Control buildToolBar() {
        var loadGame = new Button("Load Game");
        loadGame.setOnAction(_ -> getListener().onLoadGameClicked());

        archiveChooser.setPromptText("Select archive to load");
        archiveChooser.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> selectArchive(newValue));

        var pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);

        var exportButton = new Button("Export");
        exportButton.setOnAction(_ -> getListener().onExportClicked());

        var sidePane = new ToggleGroup();

        previewButton.setToggleGroup(sidePane);
        previewButton.selectedProperty().addListener((_, _, newValue) -> showPreview(newValue));

        settingsButton.setToggleGroup(sidePane);
        settingsButton.selectedProperty().addListener((_, _, newValue) -> showSettings(newValue));

        return new ToolBar(
            loadGame, archiveChooser,
            pane,
            exportButton,
            new Separator(),
            previewButton, settingsButton
        );
    }

    private void showPreview(Boolean newValue) {
        if (suppressToggleEvents) {
            return;
        }
        setSidePanelEnabled(newValue, tabPane);
        getListener().onPreviewVisibilityChanged(newValue);
    }

    private void showSettings(Boolean newValue) {
        if (suppressToggleEvents) {
            return;
        }
        setSidePanelEnabled(newValue, settingsPresenter.getView().getFXNode());
        getListener().onSettingsVisibilityChanged(newValue);
    }

    // endregion

}
