package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.event.*;
import be.twofold.valen.ui.component.*;
import be.twofold.valen.ui.component.filelist.*;
import be.twofold.valen.ui.component.preview.*;
import jakarta.inject.*;
import javafx.animation.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.slf4j.*;

import java.util.*;
import java.util.function.*;

@Singleton
public final class MainFXView implements MainView, FXView {
    private static final Logger log = LoggerFactory.getLogger(MainFXView.class);

    private final BorderPane view = new BorderPane();
    private final SplitPane splitPane = new SplitPane();

    private final ComboBox<String> archiveChooser = new ComboBox<>();
    private final TextField searchTextField = new TextField();
    private final ProgressBar progressBar = new ProgressBar();

    private final PreviewTabPane tabPane;
    private final Parent settingsView;
    private final SendChannel<MainViewEvent> channel;

    @Inject
    MainFXView(FileListFXView fileListView, PreviewTabPane tabPane, EventBus eventBus) {
        this.tabPane = tabPane;
        this.settingsView = ViewLoader.INSTANCE.load("/fxml/Settings.fxml");
        this.channel = eventBus.senderFor(MainViewEvent.class);

        buildUI();

        splitPane.getItems().add(fileListView.getFXNode());
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
        archiveChooser.getSelectionModel().select(0);
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
            log.info("Exporting: {}", exporting);
            view.setDisable(exporting);
            progressBar.setVisible(exporting);
        });
    }

    private void selectArchive(String archiveName) {
        channel.send(new MainViewEvent.ArchiveSelected(archiveName));
    }

    private void setPreviewEnabled(boolean enabled) {
        setSidePanelEnabled(enabled, tabPane, MainViewEvent.PreviewVisibilityChanged::new);
    }

    private void setSettingsEnabled(boolean enabled) {
        setSidePanelEnabled(enabled, settingsView, MainViewEvent.SettingVisibilityChanged::new);
    }

    private void setSidePanelEnabled(boolean enabled, Node node, Function<Boolean, MainViewEvent> eventFunction) {
        if (enabled) {
            if (isSidePaneVisible()) {
                return;
            }
            splitPane.getItems().add(node);
            splitPane.setDividerPositions(0.60);
            channel.send(eventFunction.apply(true));
        } else {
            if (!isSidePaneVisible()) {
                return;
            }
            splitPane.getItems().remove(1);
            splitPane.setDividerPositions();
            channel.send(eventFunction.apply(false));
        }
    }

    // region UI

    private void buildUI() {
        view.setPrefSize(1200, 800);
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
            pause.setOnFinished(_ -> channel.send(new MainViewEvent.SearchChanged(newValue)));
            pause.playFromStart();
        });

        var searchClearButton = new Button("Clear");
        searchClearButton.setDisable(true);
        searchClearButton.setOnAction(_ -> searchTextField.setText(""));
        searchClearButton.disableProperty().bind(searchTextField.textProperty().isEmpty());

        var pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);

        progressBar.setVisible(false);
        progressBar.setPrefWidth(200);

        var hBox = new HBox(
            searchTextField, searchClearButton,
            pane,
            progressBar
        );
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(5.0);
        hBox.setPadding(new Insets(3.0));
        return hBox;
    }

    private Control buildToolBar() {
        var loadGame = new Button("Load Game");
        loadGame.setOnAction(_ -> channel.send(new MainViewEvent.LoadGameClicked()));

        archiveChooser.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> selectArchive(newValue));

        var pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);

        var exportButton = new Button("Export");
        exportButton.setOnAction(_ -> {
            channel.send(new MainViewEvent.ExportClicked());
        });

        var sidePane = new ToggleGroup();

        var previewButton = new ToggleButton("Preview");
        previewButton.setToggleGroup(sidePane);
        previewButton.selectedProperty().addListener((_, _, newValue) -> setPreviewEnabled(newValue));

        var settingsButton = new ToggleButton("Settings");
        settingsButton.setToggleGroup(sidePane);
        settingsButton.selectedProperty().addListener((_, _, newValue) -> setSettingsEnabled(newValue));

        return new ToolBar(
            loadGame, archiveChooser,
            pane,
            exportButton,
            new Separator(),
            previewButton, settingsButton
        );
    }

    // endregion

}
