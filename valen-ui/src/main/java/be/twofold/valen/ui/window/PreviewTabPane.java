package be.twofold.valen.ui.window;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.viewer.*;
import jakarta.inject.*;
import javafx.scene.control.*;

import java.util.*;

public class PreviewTabPane extends TabPane {
    final List<PreviewTab> viewers;

    @Inject
    public PreviewTabPane(Set<Viewer> viewers) {
        this.viewers = viewers.stream()
            .map(PreviewTab::new)
            .toList();
    }

    public void setData(AssetType type, Object assetData) {
        getTabs().removeIf(tab -> {
            var viewer = ((PreviewTab) tab).getViewer();
            if (!viewer.canPreview(type)) {
                viewer.setData(null);
                return true;
            }
            return false;
        });

        for (PreviewTab tab : viewers) {
            if (!tab.getViewer().canPreview(type)) {
                continue;
            }
            if (!getTabs().contains(tab)) {
                getTabs().add(tab);
            }
            tab.getViewer().setData(assetData);
        }

        getTabs().sort(Comparator.comparing(Tab::getText));
        getSelectionModel().selectLast();
    }
}
