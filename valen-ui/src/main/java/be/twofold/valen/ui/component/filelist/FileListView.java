package be.twofold.valen.ui.component.filelist;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;

import java.util.*;

public interface FileListView extends View<FileListViewListener> {

    void setFileTree(PathNode<PathCombo> tree);

    void setFilteredAssets(List<Asset> assets);

    List<Asset> getSelectedAssets();

}
