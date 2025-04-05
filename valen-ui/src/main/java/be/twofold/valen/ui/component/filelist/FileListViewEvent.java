package be.twofold.valen.ui.component.filelist;

import be.twofold.valen.core.game.*;

sealed interface FileListViewEvent {
    record AssetSelected(Asset asset, boolean forced) implements FileListViewEvent {
    }

    record PathSelected(String path) implements FileListViewEvent {
    }

    record PathExportRequested(String path, boolean recursive) implements FileListViewEvent {
    }
}
