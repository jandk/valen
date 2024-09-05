package be.twofold.valen.ui.viewers.data;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.viewers.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;

import java.lang.reflect.*;
import java.util.*;

public class DataViewer extends TreeView<DataViewer.PreviewItem> implements Viewer {
    public DataViewer() {
        setCellFactory(p -> new PreviewItemTreeCellImpl());
    }

    @Override
    public boolean canPreview(AssetType type) {
        return type == AssetType.Image || type == AssetType.Text || type == AssetType.Model; //TODO: Add other supported types
    }

    @Override
    public void setData(Object data) {
        if (data.getClass().isRecord()) {
            var rootItem = new PreviewValueTreeItem(new PreviewItem(data.getClass().getSimpleName(), data));
            rootItem.setExpanded(true);
            setRoot(rootItem);
        } else if (data instanceof Map<?, ?> map) {
            var rootItem = new PreviewValueTreeItem(new PreviewItem(data.getClass().getSimpleName(), map));
            rootItem.setExpanded(true);
            setRoot(rootItem);
        } else {
            setRoot(null);
        }
    }

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public String getName() {
        return "Data";
    }

    record PreviewItem(String name, Object value) {
    }

    static class PreviewValueTreeItem extends TreeItem<PreviewItem> {
        private boolean isLeaf;
        private boolean isFirstTimeChildren = true;
        private boolean isFirstTimeLeaf = true;

        public PreviewValueTreeItem(PreviewItem value) {
            super(value);
        }

        @Override
        public ObservableList<TreeItem<PreviewItem>> getChildren() {
            if (isFirstTimeChildren) {
                isFirstTimeChildren = false;
                try {
                    super.getChildren().setAll(buildChildren(this));
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return super.getChildren();
        }

        @Override
        public boolean isLeaf() {
            if (isFirstTimeLeaf) {
                isFirstTimeLeaf = false;
                var holder = getValue();
                if (holder.getClass().isRecord()) {
                    isLeaf = false;
                } else {
                    switch (holder.value()) {
                        case Map<?, ?> ignored -> isLeaf = false;
                        case List<?> ignored -> isLeaf = false;
                        case null, default -> isLeaf = true;
                    }
                }
            }
            return isLeaf;
        }

        private ObservableList<PreviewValueTreeItem> buildChildren(PreviewValueTreeItem item) throws InvocationTargetException, IllegalAccessException {
            var holder = item.getValue();
            if (holder.value() != null) {
                ObservableList<PreviewValueTreeItem> children = FXCollections.observableArrayList();
                if (holder.value().getClass().isRecord()) {
                    var value = holder.value();
                    for (RecordComponent recordComponent : value.getClass().getRecordComponents()) {
                        children.add(new PreviewValueTreeItem(new PreviewItem(recordComponent.getName(), recordComponent.getAccessor().invoke(value))));
                    }
                } else {
                    switch (holder.value()) {
                        case Map<?, ?> map -> {
                            map.forEach((key, value) -> {
                                children.add(new PreviewValueTreeItem(new PreviewItem(key.toString(), value)));
                            });
                        }
                        case List<?> list -> {
                            for (int i = 0; i < list.size(); i++) {
                                children.add(new PreviewValueTreeItem(new PreviewItem(Integer.toString(i), list.get(i))));
                            }
                        }
                        default -> {
                            return FXCollections.emptyObservableList();
                        }
                    }
                }
                return children;
            }
            return FXCollections.emptyObservableList();
        }
    }

    static final class PreviewItemTreeCellImpl extends TreeCell<PreviewItem> {

        @Override
        public void updateItem(PreviewItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(getString());
                setGraphic(getTreeItem().getGraphic());
            }
        }

        private String getString() {
            var item = getItem();
            if (item == null) {
                return "null";
            }


            String strValue;
            if (item.value == null) {
                strValue = "null";
            } else if (item.value().getClass().isArray()) {
                strValue = "%s[%d]".formatted(item.value().getClass().componentType().getSimpleName(), Array.getLength(item.value()));
            } else {
                strValue = item.value().toString();
            }

            return "%s: %s".formatted(item.name(), strValue);
        }
    }
}