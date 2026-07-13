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

    public PreviewData decode(AssetType type, Object assetData, Meta.Node metaNode) {
        var items = new ArrayList<Decoded>();
        for (PreviewTab tab : viewers) {
            var viewer = tab.getViewer();
            if (!canShow(viewer, type, metaNode)) {
                continue;
            }
            var input = viewer instanceof MetaPresenter ? metaNode : assetData;
            items.add(new Decoded(tab, viewer.decode(input)));
        }
        return new PreviewData(items);
    }

    public void display(PreviewData data) {
        var wanted = data.items().stream()
            .map(Decoded::tab)
            .collect(Collectors.toSet());

        // Remove tabs that are no longer shown, clearing their viewers.
        getTabs().removeIf(t -> {
            var tab = (PreviewTab) t;
            if (!wanted.contains(tab)) {
                tab.getViewer().display(null);
                return true;
            }
            return false;
        });

        // Insert newly-shown tabs at their position in viewer order. Existing
        // tabs stay in place, so they don't replay the add/remove animation.
        var items = data.items();
        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            if (!getTabs().contains(item.tab())) {
                getTabs().add(i, item.tab());
            }
            item.tab().getViewer().display(item.payload());
        }

        getSelectionModel().selectFirst();
    }

    private boolean canShow(Viewer viewer, AssetType type, Meta.Node metaNode) {
        if (viewer instanceof MetaPresenter) {
            return metaNode != null;
        }
        return viewer.canPreview(type);
    }

    public record PreviewData(List<Decoded> items) {
    }

    private record Decoded(PreviewTab tab, Object payload) {
    }
}
