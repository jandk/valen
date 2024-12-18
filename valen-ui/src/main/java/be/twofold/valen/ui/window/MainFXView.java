package be.twofold.valen.ui.window;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.*;
import be.twofold.valen.ui.event.*;
import be.twofold.valen.ui.util.*;
import jakarta.inject.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.slf4j.*;

import java.util.*;
import java.util.stream.*;

public final class MainFXView implements MainView, FXView {
    private static final Logger log = LoggerFactory.getLogger(MainFXView.class);

    private final BorderPane view = new BorderPane();
    private final SplitPane splitPane = new SplitPane();

    private final ComboBox<String> archiveChooser = new ComboBox<>();
    private final TreeView<String> treeView = new TreeView<>();
    private final TableView<Asset> tableView = new TableView<>();
    private final TextField searchTextField = new TextField();
    private final ProgressBar progressBar = new ProgressBar();

    private final PreviewTabPane tabPane;
    private final SendChannel<MainViewEvent> channel;

    @Inject
    MainFXView(PreviewTabPane tabPane, EventBus eventBus) {
        this.tabPane = tabPane;
        this.channel = eventBus.senderFor(MainViewEvent.class);
        buildUI();
    }

    @Override
    public Parent getFXNode() {
        return view;
    }

    @Override
    public boolean isPreviewVisible() {
        return splitPane.getItems().size() == 3;
    }

    @Override
    public void setArchives(List<String> archives) {
        archiveChooser.getItems().setAll(archives);
    }

    @Override
    public void setFileTree(PathNode<String> tree) {
        var root = convert(tree);
        treeView.setRoot(root);
        treeView.getSelectionModel().select(root);
        root.setExpanded(true);
    }

    @Override
    public void setFilteredAssets(List<Asset> assets) {
        tableView.getItems().setAll(assets);
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

    private void selectPath(TreeItem<String> treeItem) {
        if (treeItem == null) {
            return;
        }
        var parts = new ArrayList<String>();
        for (var item = treeItem; item != null; item = item.getParent()) {
            parts.add(item.getValue().replace("\u2009", ""));
        }
        Collections.reverse(parts);
        var path = String.join("/", parts.subList(1, parts.size()));
        channel.send(new MainViewEvent.PathSelected(path));
    }

    private void selectArchive(String archiveName) {
        channel.send(new MainViewEvent.ArchiveSelected(archiveName));
    }

    private void selectAsset(Asset asset) {
        if (asset == null) {
            return;
        }
        channel.send(new MainViewEvent.AssetSelected(asset));
    }

    private void setPreviewEnabled(boolean enabled) {
        if (enabled) {
            if (splitPane.getItems().size() != 2) {
                return;
            }
            splitPane.getItems().add(tabPane);
            splitPane.setDividerPositions(splitPane.getDividerPositions()[0], 0.60);
            channel.send(new MainViewEvent.PreviewVisibilityChanged(true));
        } else {
            if (splitPane.getItems().size() != 3) {
                return;
            }
            splitPane.getItems().remove(2);
            splitPane.setDividerPositions(splitPane.getDividerPositions()[0]);
            channel.send(new MainViewEvent.PreviewVisibilityChanged(false));
        }
    }

    private TreeItem<String> convert(PathNode<String> node) {
        if (node.children().size() == 1 && !node.hasFiles()) {
            var child = convert(node.children().values().iterator().next());
            child.setValue(node.name() + "\u2009/\u2009" + child.getValue());
            return child;
        }

        var children = node.children().entrySet().stream()
            .sorted(Map.Entry.comparingByKey(NaturalOrderComparator.instance()))
            .map(Map.Entry::getValue)
            .toList();

        var item = new TreeItem<>(node.name());
        for (var child : children) {
            item.getChildren().add(convert(child));
        }
        return item;
    }

    // region UI

    private void buildUI() {
        view.setPrefSize(1200, 800);
        view.setTop(buildToolBar());
        view.setCenter(buildMainContent());
        view.setBottom(buildStatusBar());
    }

    private SplitPane buildMainContent() {
        splitPane.setDividerPositions(0.25);
        splitPane.getItems().addAll(
            buildTreeView(),
            buildTableView()
        );
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        return splitPane;
    }

    private TreeView<String> buildTreeView() {
        treeView.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> selectPath(newValue));
        return treeView;
    }

    private TableView<Asset> buildTableView() {
        tableView.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> selectAsset(newValue));
        var nameColumn = new TableColumn<Asset, String>();
        nameColumn.setText("Name");
        nameColumn.setPrefWidth(160);
        nameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().id().fullName()));

        var typeColumn = new TableColumn<Asset, String>();
        typeColumn.setText("Type");
        typeColumn.setPrefWidth(40);
        typeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().type().name()));

        var propertiesColumn = new TableColumn<Asset, String>();
        propertiesColumn.setText("Properties");
        propertiesColumn.setPrefWidth(80);
        propertiesColumn.setCellValueFactory(param -> mapPropertiesColumn(param.getValue()));

        tableView.getColumns().addAll(nameColumn, typeColumn, propertiesColumn);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN); // TODO: Maybe change this?
        return tableView;
    }

    private ObservableStringValue mapPropertiesColumn(Asset asset) {
        var stringified = asset.properties().entrySet().stream()
            .filter(e -> Character.isUpperCase(e.getKey().charAt(0)))
            .sorted(Map.Entry.comparingByKey())
            .map(e -> e.getKey() + ": " + e.getValue())
            .collect(Collectors.joining(", "));

        return new ReadOnlyStringWrapper(stringified);
    }

    private HBox buildStatusBar() {
        searchTextField.setId("searchTextField");
        searchTextField.setPromptText("Search");

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
            var selectedAsset = tableView.getSelectionModel().getSelectedItem();
            if (selectedAsset != null) {
                channel.send(new MainViewEvent.ExportClicked(selectedAsset));
            }
        });

        var previewButton = new ToggleButton("Preview");
        previewButton.selectedProperty().addListener((_, _, newValue) -> setPreviewEnabled(newValue));

        var settingsButton = new Button("Settings");
        settingsButton.setDisable(true);

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
