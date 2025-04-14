package be.twofold.valen.ui.component.filelist;

import backbonefx.event.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.component.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.fxml.*;
import javafx.scene.control.*;

import java.util.*;
import java.util.stream.*;

public final class FileListController implements Controller {
    private static final String SPACE = "\u2009";
    private static final String SEPARATOR = SPACE + "/" + SPACE;

    private @FXML TreeView<PathCombo> treeView;
    private @FXML TableView<Asset> tableView;
    private @FXML TableColumn<Asset, String> columnName;
    private @FXML TableColumn<Asset, String> columnType;
    private @FXML TableColumn<Asset, String> columnProp;

    private Map<String, List<Asset>> assetIndex = Map.of();
    private final EventBus eventBus;

    public FileListController(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void initialize() {
        treeView.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> selectPath(newValue));
        treeView.setCellFactory(_ -> new TreeCell<>() {
            @Override
            protected void updateItem(PathCombo item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null || empty ? null : item.name());
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> selectAsset(newValue));
        columnName.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().id().displayName()));
        columnType.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().type().getName()));
        columnProp.setCellValueFactory(param -> mapPropertiesColumn(param.getValue()));
    }

    public void setAssets(Stream<? extends Asset> assets) {
        assetIndex = assets.collect(Collectors.groupingBy(asset -> asset.id().pathName()));
        setFileTree(buildPathTree());
    }

    private ObservableStringValue mapPropertiesColumn(Asset asset) {
        var stringValue = asset.properties().entrySet().stream()
            .filter(e -> Character.isUpperCase(e.getKey().charAt(0)))
            .sorted(Map.Entry.comparingByKey())
            .map(e -> e.getKey() + ": " + e.getValue())
            .collect(Collectors.joining(", "));

        return new ReadOnlyStringWrapper(stringValue);
    }

    private void selectPath(TreeItem<PathCombo> path) {
        var assets = assetIndex.getOrDefault(path.getValue().full(), List.of()).stream().sorted().toList();
        Platform.runLater(() -> tableView.getItems().setAll(assets));
    }

    private void selectAsset(Asset asset) {
        if (asset != null) {
            eventBus.publish(new FileListViewEvent.AssetSelected(asset, false));
        }
    }

    // region Tree

    private void setFileTree(PathNode<PathCombo> tree) {
        var root = pathNodeToTreeItem(tree);
        Platform.runLater(() -> transferSelectedItem(root));
    }

    private PathNode<PathCombo> buildPathTree() {
        var paths = assetIndex.keySet().stream().sorted().toList();

        var root = new PathNode<>(new PathCombo("", "root"), true);
        for (var path : paths) {
            if (path.isBlank()) {
                continue;
            }

            var indices = new ArrayList<Integer>();
            indices.add(-1);
            for (int i = 0; i < path.length(); i++) {
                if (path.charAt(i) == '/') {
                    indices.add(i);
                }
            }
            indices.add(path.length());

            var node = root;
            for (var i = 0; i < indices.size() - 1; i++) {
                var part = path.substring(indices.get(i) + 1, indices.get(i + 1));
                node = node.get(
                    new PathCombo(path.substring(0, indices.get(i + 1)), part),
                    i == indices.size() - 2
                );
            }
        }
        return root;
    }

    private TreeItem<PathCombo> pathNodeToTreeItem(PathNode<PathCombo> node) {
        if (node.children().size() == 1 && !node.hasFiles()) {
            var child = pathNodeToTreeItem(node.children().values().iterator().next());
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
            item.getChildren().add(pathNodeToTreeItem(child));
        }
        return item;
    }

    private void transferSelectedItem(TreeItem<PathCombo> root) {
        var selectedItem = treeView.getSelectionModel().getSelectedItem();
        var selectedValue = selectedItem != null ? selectedItem.getValue() : null;
        var selectedTreeItem = findSelectedValue(root, selectedValue);

        treeView.setRoot(root);
        if (selectedTreeItem != null) {
            selectedTreeItem.setExpanded(selectedItem != null && selectedItem.isExpanded());
            treeView.getSelectionModel().select(selectedTreeItem);
        } else {
            treeView.getSelectionModel().select(root);
        }
        root.setExpanded(true);
    }

    private <T> TreeItem<T> findSelectedValue(TreeItem<T> item, T selectedValue) {
        if (selectedValue == null) {
            return item;
        }
        for (var child : item.getChildren()) {
            if (child.getValue().equals(selectedValue)) {
                return child;
            }
            var found = findSelectedValue(child, selectedValue);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    // endregion

}
