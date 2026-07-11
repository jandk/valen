package be.twofold.valen.ui.component.preview;

import backbonefx.di.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.component.*;
import be.twofold.valen.ui.component.metaview.*;
import be.twofold.valen.ui.component.modelviewer.*;
import be.twofold.valen.ui.component.rawview.*;
import be.twofold.valen.ui.component.textureviewer.*;
import jakarta.inject.*;
import javafx.scene.control.*;

import java.util.*;
import java.util.stream.*;

public final class PreviewTabPane extends TabPane {
    private final List<PreviewTab> viewers;

    @Inject
    PreviewTabPane(Feather feather) {
        this.viewers = Stream.of(
                feather.instance(ModelPresenter.class),
                feather.instance(TexturePresenter.class),
                feather.instance(MetaPresenter.class),
                feather.instance(RawPresenter.class))
            .map(PreviewTab::new)
            .toList();
    }

    public void setData(AssetType type, Object assetData, Meta.Node metaNode) {
        getTabs().removeIf(tab -> {
            var viewer = ((PreviewTab) tab).getViewer();
            if (!canShow(viewer, type, metaNode)) {
                viewer.setData(null);
                return true;
            }
            return false;
        });

        for (PreviewTab tab : viewers) {
            if (!canShow(tab.getViewer(), type, metaNode)) {
                continue;
            }
            if (!getTabs().contains(tab)) {
                getTabs().add(tab);
            }
            if (tab.getViewer() instanceof MetaPresenter) {
                tab.getViewer().setData(metaNode);
            } else {
                tab.getViewer().setData(assetData);
            }
        }

        getTabs().sort(Comparator.comparingInt(viewers::indexOf));
        getSelectionModel().selectFirst();
    }

    private boolean canShow(Viewer viewer, AssetType type, Meta.Node metaNode) {
        if (viewer instanceof MetaPresenter) {
            return metaNode != null;
        }
        return viewer.canPreview(type);
    }
}
