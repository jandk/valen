package be.twofold.valen.ui.viewer.data;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.viewer.*;
import javafx.scene.*;
import javafx.scene.control.*;

import java.util.*;

public final class DataViewer extends TreeView<PreviewItem> implements Viewer {
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

}