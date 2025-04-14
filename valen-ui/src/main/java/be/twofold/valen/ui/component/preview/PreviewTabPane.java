package be.twofold.valen.ui.component.preview;

import backbonefx.di.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.component.modelviewer.*;
import be.twofold.valen.ui.component.rawview.*;
import be.twofold.valen.ui.component.textureviewer.*;
import jakarta.inject.*;
import javafx.scene.control.*;

import java.util.*;
import java.util.stream.*;

public final class PreviewTabPane extends TabPane {
    final List<PreviewTab> viewers;

    @Inject
    PreviewTabPane(Feather feather) {
        this.viewers = Stream.of(
                feather.instance(ModelPresenter.class),
                feather.instance(TexturePresenter.class),
                feather.instance(RawPresenter.class))
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
