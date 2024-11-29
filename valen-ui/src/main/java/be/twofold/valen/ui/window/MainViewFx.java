package be.twofold.valen.ui.window;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.*;
import jakarta.inject.*;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

import java.util.*;

public final class MainViewFx extends AbstractView<MainViewListener> implements MainView {
    private final BorderPane view = new BorderPane();
    private final SplitPane splitPane = new SplitPane();

    private final ComboBox<String> archiveChooser = new ComboBox<>();
    private final TreeView<String> treeView = new TreeView<>();
    private final TableView<Asset> tableView = new TableView<>();

    private final PreviewTabPane tabPane;

    @Inject
    public MainViewFx(PreviewTabPane tabPane) {
        super(MainViewListener.class);
        this.tabPane = tabPane;
        buildUI();
    }

    @Override
    public Parent getView() {
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
    public void setFileTree(TreeItem<String> root) {
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

    private void selectPath(TreeItem<String> treeItem) {
        if (treeItem == null) {
            return;
        }
        List<String> path = new ArrayList<>();
        for (var item = treeItem; item != null; item = item.getParent()) {
            path.add(item.getValue());
        }
        Collections.reverse(path);
        listeners().fire().onPathSelected(String.join("/", path.subList(1, path.size())));
    }

    private void selectArchive(String archiveName) {
        listeners().fire().onArchiveSelected(archiveName);
    }

    private void selectAsset(Asset newValue) {
        if (newValue == null) {
            return;
        }
        listeners().fire().onAssetSelected(newValue);
    }

    private void setPreviewEnabled(boolean enabled) {
        if (enabled) {
            if (splitPane.getItems().size() != 2) {
                return;
            }
            splitPane.getItems().add(tabPane);
            splitPane.setDividerPositions(splitPane.getDividerPositions()[0], 0.60);
            listeners().fire().onPreviewVisibleChanged(true);
        } else {
            if (splitPane.getItems().size() != 3) {
                return;
            }
            splitPane.getItems().remove(2);
            splitPane.setDividerPositions(splitPane.getDividerPositions()[0]);
            listeners().fire().onPreviewVisibleChanged(false);
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
        TableColumn<Asset, String> nameColumn = new TableColumn<>();
        nameColumn.setText("Name");
        nameColumn.setPrefWidth(200);
        nameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().id().fileName()));

        TableColumn<Asset, String> typeColumn = new TableColumn<>();
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
        var searchTextField = new TextField();
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

        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);

        var previewButton = new ToggleButton("Preview");
        previewButton.selectedProperty().addListener((_, _, newValue) -> {
            setPreviewEnabled(newValue);
        });

        return new ToolBar(
            new Button("Load Game"),
            archiveChooser,
            pane,
            previewButton,
            new Button("Settings")
        );
    }

    // endregion

}
