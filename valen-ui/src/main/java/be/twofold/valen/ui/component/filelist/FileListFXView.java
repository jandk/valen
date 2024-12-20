package be.twofold.valen.ui.component.filelist;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.event.*;
import jakarta.inject.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;
import java.util.stream.*;

@Singleton
public final class FileListFXView implements FileListView, FXView {
    private static final String SPACE = "\u2009";
    private static final String SEPARATOR = SPACE + "/" + SPACE;

    private final SplitPane splitPane = new SplitPane();
    private final TreeView<String> treeView = new TreeView<>();
    private final TableView<Asset> tableView = new TableView<>();

    private final SendChannel<FileListViewEvent> channel;

    @Inject
    FileListFXView(EventBus eventBus) {
        this.channel = eventBus.senderFor(FileListViewEvent.class);
        buildUI();
    }

    @Override
    public Parent getFXNode() {
        return splitPane;
    }

    @Override
    public void setFileTree(PathNode<String> tree) {
        var root = convert(tree);
        Platform.runLater(() -> {
            treeView.setRoot(root);
            treeView.getSelectionModel().select(root);
            root.setExpanded(true);
        });
    }

    @Override
    public void setFilteredAssets(List<Asset> assets) {
        Platform.runLater(() -> tableView.getItems().setAll(assets));
    }

    @Override
    public Asset getSelectedAsset() {
        return tableView.getSelectionModel().getSelectedItem();
    }

    private void selectPath(TreeItem<String> treeItem) {
        if (treeItem == null) {
            return;
        }
        var parts = new ArrayList<String>();
        for (var item = treeItem; item != null; item = item.getParent()) {
            parts.add(item.getValue().replace(SEPARATOR, "/"));
        }
        Collections.reverse(parts);
        var path = String.join("/", parts.subList(1, parts.size()));
        channel.send(new FileListViewEvent.PathSelected(path));
    }

    private void selectAsset(Asset asset) {
        if (asset == null) {
            return;
        }
        channel.send(new FileListViewEvent.AssetSelected(asset));
    }

    private TreeItem<String> convert(PathNode<String> node) {
        if (node.children().size() == 1 && !node.hasFiles()) {
            var child = convert(node.children().values().iterator().next());
            child.setValue(node.name() + SEPARATOR + child.getValue());
            return child;
        }

        var children = node.children().entrySet().stream()
            .sorted(Map.Entry.comparingByKey(AlphanumericComparator.instance()))
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
        splitPane.setDividerPositions(0.25);
        splitPane.getItems().addAll(
            buildTreeView(),
            buildTableView()
        );
        // How is it that the exact method I need exists?
        SplitPane.setResizableWithParent(splitPane.getItems().getFirst(), false);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
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
        nameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().id().fileName()));

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

    // endregion

}
