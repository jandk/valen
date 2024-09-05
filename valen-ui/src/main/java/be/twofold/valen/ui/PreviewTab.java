package be.twofold.valen.ui;

import be.twofold.valen.ui.viewers.*;
import javafx.scene.control.*;

public class PreviewTab extends Tab {
    private final Viewer viewer;

    public PreviewTab(Viewer viewer) {
        super(viewer.getName(), viewer.getNode());
        this.viewer = viewer;
        setClosable(false);
    }

    public Viewer getViewer() {
        return viewer;
    }
}
