package be.twofold.valen.ui.component.filelist;

import be.twofold.valen.core.game.*;

import java.util.*;

public interface FileListView {

    void setFileTree(PathNode<PathCombo> tree);

    void setFilteredAssets(List<Asset> assets);

    List<Asset> getSelectedAssets();

}
