package be.twofold.valen.ui.component.dataviewer;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.component.*;
import jakarta.inject.*;
import javafx.scene.*;
import javafx.scene.control.*;

import java.util.*;

public final class DataViewer extends TreeView<PreviewItem> implements Viewer {
    @Inject
    public DataViewer() {
        setCellFactory(_ -> new PreviewItemTreeCellImpl());
    }

    @Override
    public boolean canPreview(AssetType type) {
        return type == AssetType.MODEL
            || type == AssetType.TEXTURE;
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
    public Parent getFXNode() {
        return this;
    }

    @Override
    public String getName() {
        return "Data";
    }
}
