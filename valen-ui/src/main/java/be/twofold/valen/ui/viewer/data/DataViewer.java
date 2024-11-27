package be.twofold.valen.ui.viewer.data;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.viewer.*;
import jakarta.inject.*;
import javafx.scene.*;
import javafx.scene.control.*;

import java.util.*;

public final class DataViewer extends TreeView<PreviewItem> implements Viewer {
    @Inject
    public DataViewer() {
        setCellFactory(p -> new PreviewItemTreeCellImpl());
    }

    @Override
    public boolean canPreview(AssetType type) {
        // TODO: Add other supported types
        return type == AssetType.Texture || type == AssetType.Text || type == AssetType.Model;
    }

    @Override
    public void setData(Object data) {
        if (data == null) {
            return;
        }
        if (data.getClass().isRecord() || data instanceof Map<?, ?>) {
            var rootItem = new PreviewValueTreeItem(new PreviewItem(data.getClass().getSimpleName(), data));
            rootItem.setExpanded(true);
            setRoot(rootItem);
            return;
        }
        setRoot(null);
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
