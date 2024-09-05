package be.twofold.valen.ui;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.viewers.*;
import be.twofold.valen.ui.viewers.data.*;
import be.twofold.valen.ui.viewers.image.*;
import javafx.scene.control.*;

import java.util.*;

public class PreviewTabPane extends TabPane {
    final List<PreviewTab> viewers;

    public PreviewTabPane() {
        viewers = List.of(
            new PreviewTab(new ImageViewer()),
            new PreviewTab(new DataViewer()),
            new PreviewTab(new TextViewer())
        );
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
    }
}
