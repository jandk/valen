package be.twofold.valen.ui.component.filelist;

import backbonefx.event.*;
import be.twofold.valen.core.game.*;

sealed interface FileListViewEvent extends Event {
    record AssetSelected(Asset asset, boolean forced) implements FileListViewEvent {
    }

    record PathSelected(String path) implements FileListViewEvent {
    }

    record PathExportRequested(String path, boolean recursive) implements FileListViewEvent {
    }
}
