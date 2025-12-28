package be.twofold.valen.ui.component.preview;

import be.twofold.valen.ui.component.*;
import javafx.scene.control.*;

final class PreviewTab extends Tab {
    private final Viewer viewer;

    PreviewTab(Viewer viewer) {
        super(viewer.getName(), viewer.getFXNode());
        this.viewer = viewer;
        setClosable(false);
    }

    Viewer getViewer() {
        return viewer;
    }
}
