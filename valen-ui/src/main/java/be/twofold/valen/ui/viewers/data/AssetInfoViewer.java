package be.twofold.valen.ui.viewers.data;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.viewers.*;
import javafx.scene.*;
import javafx.scene.control.*;

public class AssetInfoViewer extends TreeView<PreviewItem> implements Viewer {
    public AssetInfoViewer() {
        setCellFactory(p -> new PreviewItemTreeCellImpl());
    }

    @Override
    public boolean canPreview(Asset asset) {
        return true; //TODO: Add other supported types
    }

    @Override
    public boolean setData(Asset asset, Archive archive) {
        var rootItem = new PreviewValueTreeItem(new PreviewItem(asset.getClass().getSimpleName(), asset));
        setRoot(rootItem);
        return true;
    }

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public String getName() {
        return "Asset info";
    }

}