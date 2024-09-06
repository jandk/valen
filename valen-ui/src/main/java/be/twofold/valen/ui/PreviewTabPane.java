package be.twofold.valen.ui;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.viewers.*;
import be.twofold.valen.ui.viewers.data.*;
import be.twofold.valen.ui.viewers.image.*;
import javafx.scene.control.*;

import java.io.*;
import java.util.*;

public class PreviewTabPane extends TabPane {
    final List<PreviewTab> viewers;

    public PreviewTabPane() {
        viewers = List.of(
            new PreviewTab(new ImageViewer()),
            new PreviewTab(new AssetInfoViewer()),
            new PreviewTab(new DataViewer()),
            new PreviewTab(new TextViewer())
        );
    }

    public void setData(Asset asset, Archive archive) {
        getTabs().removeIf(tab -> {
            var viewer = ((PreviewTab) tab).getViewer();
            if (!viewer.canPreview(asset)) {
                try {
                    viewer.setData(null, null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
            return false;
        });

        for (PreviewTab tab : viewers) {
            if (!tab.getViewer().canPreview(asset)) {
                continue;
            }
            if (!getTabs().contains(tab)) {
                getTabs().add(tab);
            }
            try {
                tab.getViewer().setData(asset, archive);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        getTabs().sort(Comparator.comparing(Tab::getText));
    }
}
