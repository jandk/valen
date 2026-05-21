package be.twofold.valen.ui.component.filelist;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;

import java.util.*;

public interface FileListView extends View<FileListView.Listener> {

    void setFileTree(PathNode<PathCombo> tree);

    void setFilteredAssets(List<Asset> assets);

    List<Asset> getSelectedAssets();

    interface Listener extends View.Listener {

        void onPathSelected(String path);

        void onAssetSelected(Asset asset, boolean forced);

        void onPathExportRequested(String path, boolean recursive);

    }
}
