package be.twofold.valen.ui.window;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.event.*;
import be.twofold.valen.ui.util.*;
import jakarta.inject.*;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

import java.util.*;

public final class MainViewFx implements MainView {
    private final BorderPane view = new BorderPane();
    private final SplitPane splitPane = new SplitPane();

    private final ComboBox<String> archiveChooser = new ComboBox<>();
    private final TreeView<String> treeView = new TreeView<>();
    private final TableView<Asset> tableView = new TableView<>();
    private final TextField searchTextField = new TextField();

    private final PreviewTabPane tabPane;
    private final SendChannel<MainViewEvent> channel;

    @Inject
    public MainViewFx(PreviewTabPane tabPane, EventBus eventBus) {
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

    private void selectPath(TreeItem<String> treeItem) {
        if (treeItem == null) {
            return;
        }
        List<String> parts = new ArrayList<>();
        for (var item = treeItem; item != null; item = item.getParent()) {
            parts.add(item.getValue());
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
        treeView.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            selectPath(newValue);
        });
        return treeView;
    }

    private TableView<Asset> buildTableView() {
        tableView.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            selectAsset(newValue);
        });
        var nameColumn = new TableColumn<Asset, String>();
        nameColumn.setText("Name");
        nameColumn.setPrefWidth(200);
        nameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().id().fileName()));

        var typeColumn = new TableColumn<Asset, String>();
        typeColumn.setText("Type");
        typeColumn.setPrefWidth(40);
        typeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().type().name()));

//        TableColumn<Asset<?>, Size> compressedColumn = new TableColumn<>();
//        compressedColumn.setText("Compressed");
//        compressedColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(new Size(param.getValue().compressedSize())));
//
//        TableColumn<Asset<?>, Size> uncompressedColumn = new TableColumn<>();
//        uncompressedColumn.setText("Uncompressed");
//        uncompressedColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(new Size(param.getValue().uncompressedSize())));

        tableView.getColumns().addAll(nameColumn, typeColumn/*, compressedColumn, uncompressedColumn*/);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN); // TODO: Maybe change this?
        return tableView;
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

        var rightStatus = new Label("Right status");
        rightStatus.setTextFill(Color.color(0.625, 0.625, 0.625));
        HBox.setHgrow(rightStatus, Priority.NEVER);

        var hBox = new HBox(
            searchTextField, searchClearButton,
            pane,
            rightStatus
        );
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(5.0);
        hBox.setPadding(new Insets(3.0));
        return hBox;
    }

    private Control buildToolBar() {
        archiveChooser.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            selectArchive(newValue);
        });

        var pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);

        var previewButton = new ToggleButton("Preview");
        previewButton.selectedProperty().addListener((_, _, newValue) -> {
            setPreviewEnabled(newValue);
        });

        var loadGame = new Button("Load Game");

        loadGame.setOnAction(_ -> channel.send(new MainViewEvent.LoadGameClicked()));
        loadGame.fire();
        return new ToolBar(
            loadGame,
            archiveChooser,
            pane,
            previewButton,
            new Button("Settings")
        );
    }

    // endregion

}
