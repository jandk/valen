package be.twofold.valen.ui.component.filelist;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;

public interface FileListViewListener extends ViewListener {

    void onPathSelected(String path);

    void onAssetSelected(Asset asset, boolean forced);

    void onPathExportRequested(String path, boolean recursive);

}
