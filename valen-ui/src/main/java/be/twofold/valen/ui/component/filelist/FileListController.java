package be.twofold.valen.ui.component.filelist;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.utils.*;
import jakarta.inject.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;

import java.util.*;
import java.util.stream.*;

@Singleton
public final class FileListController extends AbstractView<FileListView.Listener> implements FileListView {
    private static final String SPACE = "\u2009";
    private static final String SEPARATOR = SPACE + "/" + SPACE;

    private @FXML SplitPane splitPane;
    private @FXML TreeView<PathCombo> treeView;
    private @FXML TableView<Asset> tableView;
    private @FXML TableColumn<Asset, String> nameColumn;
    private @FXML TableColumn<Asset, String> typeColumn;
    private @FXML TableColumn<Asset, String> propertiesColumn;

    @Inject
    FileListController() {
    }

    @FXML
    private void initialize() {
        setupTreeView();
        setupTableView();
    }

    @Override
    public Parent getFXNode() {
        return splitPane;
    }

    @Override
    public void setFileTree(PathNode<PathCombo> tree) {
        var root = convert(tree);
        Platform.runLater(() -> {
            var selectedItem = treeView.getSelectionModel().getSelectedItem();
            var selectedValue = selectedItem != null ? selectedItem.getValue() : null;
            var selectedTreeItem = findSelected(root, selectedValue);

            treeView.setRoot(root);
            if (selectedTreeItem != null) {
                selectedTreeItem.setExpanded(selectedItem != null && selectedItem.isExpanded());
                treeView.getSelectionModel().select(selectedTreeItem);
            } else {
                treeView.getSelectionModel().select(root);
            }
            root.setExpanded(true);
        });
    }

    private <T> TreeItem<T> findSelected(TreeItem<T> item, T selectedValue) {
        if (selectedValue == null) {
            return item;
        }
        for (var child : item.getChildren()) {
            if (child.getValue().equals(selectedValue)) {
                return child;
            }
            var found = findSelected(child, selectedValue);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    @Override
    public void setFilteredAssets(List<Asset> assets) {
        Platform.runLater(() -> tableView.getItems().setAll(assets));
    }

    @Override
    public List<Asset> getSelectedAssets() {
        return tableView.getSelectionModel().getSelectedItems();
    }

    private void selectPath(TreeItem<PathCombo> treeItem) {
        if (treeItem != null) {
            getListener().onPathSelected(treeItem.getValue().full());
        }
    }

    private void selectAssets(List<? extends Asset> c) {
        if (c.size() == 1) {
            selectAsset(c.getFirst());
        }
    }

    private void selectAsset(Asset asset) {
        if (asset != null) {
            getListener().onAssetSelected(asset, false);
        }
    }

    private TreeItem<PathCombo> convert(PathNode<PathCombo> node) {
        if (node.children().size() == 1 && !node.hasFiles()) {
            var child = convert(node.children().values().iterator().next());
            child.setValue(new PathCombo(
                child.getValue().full(),
                node.name().name() + SEPARATOR + child.getValue().name())
            );
            return child;
        }

        var children = node.children().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(Map.Entry::getValue)
            .toList();

        var item = new TreeItem<>(node.name());
        for (var child : children) {
            item.getChildren().add(convert(child));
        }
        return item;
    }

    // region UI Setup

    private void setupTreeView() {
        treeView.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> selectPath(newValue));
        treeView.setCellFactory(_ -> new FileListTreeCell());
    }

    private void setupTableView() {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Asset>) c -> selectAssets(c.getList()));
        tableView.setRowFactory(_ -> {
            var row = new TableRow<Asset>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    getListener().onAssetSelected(row.getItem(), true);
                }
            });
            return row;
        });

        nameColumn.setCellFactory(TooltippedTableCell.forTableColumn());
        nameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().id().displayName()));

        typeColumn.setCellFactory(TooltippedTableCell.forTableColumn());
        typeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().type().displayName()));

        propertiesColumn.setCellFactory(TooltippedTableCell.forTableColumn());
        propertiesColumn.setCellValueFactory(param -> mapPropertiesColumn(param.getValue()));
    }

    private ObservableStringValue mapPropertiesColumn(Asset asset) {
        var stringified = asset.properties().entrySet().stream()
            .filter(e -> Character.isUpperCase(e.getKey().charAt(0)))
            .sorted(Map.Entry.comparingByKey())
            .map(e -> e.getKey() + ": " + e.getValue())
            .collect(Collectors.joining(", "));

        return new ReadOnlyStringWrapper(stringified);
    }

    // endregion

    private class FileListTreeCell extends TreeCell<PathCombo> {
        private final ContextMenu contextMenu;

        private FileListTreeCell() {
            var exportAll = new MenuItem("Export");
            exportAll.setOnAction(_ -> {
                getListener().onPathExportRequested(getItem().full(), false);
            });

            var exportAllSub = new MenuItem("Export recursively");
            exportAllSub.setOnAction(_ -> {
                getListener().onPathExportRequested(getItem().full(), true);
            });

            contextMenu = new ContextMenu(exportAll, exportAllSub);
        }

        @Override
        protected void updateItem(PathCombo item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setText(null);
                setContextMenu(null);
            } else {
                setText(item.toString());
                setContextMenu(contextMenu);
            }
        }
    }
}
