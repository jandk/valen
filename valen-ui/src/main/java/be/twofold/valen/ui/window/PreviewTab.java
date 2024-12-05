package be.twofold.valen.ui.window;

import be.twofold.valen.ui.viewer.*;
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
