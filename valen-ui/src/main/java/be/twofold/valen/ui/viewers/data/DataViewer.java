package be.twofold.valen.ui.viewers.data;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.viewers.*;
import javafx.scene.*;
import javafx.scene.control.*;

import java.io.*;
import java.util.*;

public class DataViewer extends TreeView<PreviewItem> implements Viewer {
    public DataViewer() {
        setCellFactory(p -> new PreviewItemTreeCellImpl());
    }

    @Override
    public boolean canPreview(Asset asset) {
        return true; //TODO: Add other supported types
    }

    @Override
    public boolean setData(Asset asset, Archive archive) throws IOException {
        Object assetData;
        try {
            assetData = archive.loadAsset(asset.id());
        } catch (IllegalArgumentException e) {
            //TODO: REDxEYE: Add logging
            e.printStackTrace();
            return false;
        }
        if (assetData != null) {
            if (assetData.getClass().isRecord()) {
                var rootItem = new PreviewValueTreeItem(new PreviewItem(assetData.getClass().getSimpleName(), assetData));
                rootItem.setExpanded(true);
                setRoot(rootItem);
                return true;
            } else if (assetData instanceof Map<?, ?> map) {
                var rootItem = new PreviewValueTreeItem(new PreviewItem(assetData.getClass().getSimpleName(), map));
                rootItem.setExpanded(true);
                setRoot(rootItem);
                return true;
            }
        }
        setRoot(null);
        return false;
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