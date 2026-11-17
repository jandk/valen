package be.twofold.valen.ui.component.main;

import backbonefx.di.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.*;
import be.twofold.valen.ui.component.preview.*;
import be.twofold.valen.ui.component.settings.*;
import jakarta.inject.*;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.*;
import org.slf4j.*;

import java.util.*;

@Singleton
public final class MainViewImpl extends AbstractView<MainView.Listener> implements MainView {
    private static final Logger log = LoggerFactory.getLogger(MainViewImpl.class);

    private final BorderPane view = new BorderPane();
    private final SplitPane splitPane = new SplitPane();

    private final ToggleButton previewButton = new ToggleButton("Preview");
    private final ToggleButton settingsButton = new ToggleButton("Settings");

    private final MenuButton gameButton = new MenuButton("Load Game");
    private final ToggleGroup gameGroup = new ToggleGroup();
    private GameEntry currentGame;
    private final ComboBox<String> archiveChooser = new ComboBox<>();
    private final TextField searchTextField = new TextField();

    private final StackPane previewPane = new StackPane();
    private final ProgressIndicator previewSpinner = new ProgressIndicator();
    private final StackPane previewVeil = new StackPane(previewSpinner);
    private final PauseTransition spinnerDelay = new PauseTransition(Duration.millis(200));

    private final PreviewTabPane previewTabPane;
    private final SettingsPresenter settingsPresenter;

    @Inject
    MainViewImpl(PreviewTabPane previewTabPane, Feather feather) {
        this.previewTabPane = previewTabPane;
        this.settingsPresenter = feather.instance(SettingsPresenter.class);

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
    public void setGames(List<GameEntry> games) {
        FxUtils.runOnFxThread(() -> {
            var items = new ArrayList<MenuItem>();
            for (var game : games) {
                var item = new RadioMenuItem(game.name());
                item.setToggleGroup(gameGroup);
                item.setUserData(game.executable());
                item.setOnAction(_ -> {
                    showCurrentGame();
                    getListener().onGameSelected(game.executable());
                });
                items.add(item);
            }
            if (!items.isEmpty()) {
                items.add(new SeparatorMenuItem());
            }
            var browse = new MenuItem("Browse for executable...");
            browse.setOnAction(_ -> getListener().onLoadGameClicked());
            items.add(browse);
            gameButton.getItems().setAll(items);
            showCurrentGame();
        });
    }

    @Override
    public void setCurrentGame(GameEntry game) {
        FxUtils.runOnFxThread(() -> {
            currentGame = game;
            showCurrentGame();
        });
    }

    private void showCurrentGame() {
        gameButton.setText(currentGame != null ? currentGame.name() : "Load Game");

        var executable = currentGame != null ? currentGame.executable() : null;
        var toggle = gameGroup.getToggles().stream()
            .filter(t -> t.getUserData().equals(executable))
            .findFirst()
            .orElse(null);
        gameGroup.selectToggle(toggle);
    }

    @Override
    public void setArchives(List<String> archives) {
        archiveChooser.getSelectionModel().clearSelection();
        archiveChooser.getItems().setAll(archives);
        // archiveChooser.getSelectionModel().select(0);
    }

    @Override
    public Object decodePreview(AssetType type, Object assetData, Meta.Node metadata) {
        // Runs on the caller's (loader) thread: decode is pure, no scene graph.
        return previewTabPane.decode(type, assetData, metadata);
    }

    @Override
    public void displayPreview(Object preview) {
        FxUtils.runOnFxThread(() -> previewTabPane.display((PreviewTabPane.PreviewData) preview));
    }

    @Override
    public void clearPreview() {
        FxUtils.runOnFxThread(previewTabPane::clear);
    }

    @Override
    public void focusOnSearch() {
        searchTextField.requestFocus();
    }

    @Override
    public void setExporting(boolean exporting) {
        FxUtils.runOnFxThread(() -> view.setDisable(exporting));
    }

    @Override
    public void setPreviewLoading(boolean loading) {
        FxUtils.runOnFxThread(() -> {
            if (loading) {
                // Delay showing the spinner so quick loads don't flash it.
                spinnerDelay.playFromStart();
            } else {
                spinnerDelay.stop();
                previewVeil.setVisible(false);
            }
        });
    }

    @Override
    public void showSidePanel(SidePanel panel) {
        FxUtils.runOnFxThread(() -> {
            previewButton.setSelected(panel == SidePanel.PREVIEW);
            settingsButton.setSelected(panel == SidePanel.SETTINGS);
            setSidePanelContent(panel);
        });
    }

    private void selectArchive(String archiveName) {
        if (archiveName == null) {
            return;
        }
        getListener().onArchiveSelected(archiveName);
    }

    private void setSidePanelContent(SidePanel panel) {
        var node = switch (panel) {
            case PREVIEW -> previewPane;
            case SETTINGS -> settingsPresenter.getView().getFXNode();
            case NONE -> null;
        };

        var items = splitPane.getItems();
        if (node == null) {
            if (items.size() == 2) {
                items.remove(1);
                splitPane.setDividerPositions();
            }
        } else if (items.size() == 2) {
            items.set(1, node);
        } else {
            items.add(node);
            splitPane.setDividerPositions(0.60);
        }
    }

    private SidePanel selectedPanel() {
        if (previewButton.isSelected()) {
            return SidePanel.PREVIEW;
        }
        if (settingsButton.isSelected()) {
            return SidePanel.SETTINGS;
        }
        return SidePanel.NONE;
    }

    // region UI

    private void buildUI() {
        buildPreviewPane();

        view.setPrefSize(1250, 666);
        view.setTop(buildToolBar());
        view.setCenter(buildMainContent());
        view.setBottom(buildStatusBar());
    }

    private void buildPreviewPane() {
        previewSpinner.setMaxSize(60, 60);

        // Block interaction while loading
        previewVeil.setVisible(false);
        previewVeil.setPickOnBounds(true);
        spinnerDelay.setOnFinished(_ -> previewVeil.setVisible(true));

        previewPane.getChildren().setAll(previewTabPane, previewVeil);
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
        archiveChooser.setPromptText("Select archive to load");
        archiveChooser.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> selectArchive(newValue));

        var pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);

        var exportButton = new Button("Export");
        exportButton.setOnAction(_ -> getListener().onExportClicked());

        var sidePane = new ToggleGroup();

        previewButton.setToggleGroup(sidePane);
        previewButton.setOnAction(_ -> getListener().onSidePanelToggled(selectedPanel()));

        settingsButton.setToggleGroup(sidePane);
        settingsButton.setOnAction(_ -> getListener().onSidePanelToggled(selectedPanel()));

        return new ToolBar(
            gameButton, archiveChooser,
            pane,
            exportButton,
            new Separator(),
            previewButton, settingsButton
        );
    }

    // endregion

}
