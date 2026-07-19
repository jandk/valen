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

/**
 * A fixed strip of preview tabs. The tabs never come and go — so the strip
 * doesn't animate on every asset change — instead, tabs that can't show the
 * current asset are disabled. The single {@code Preview} tab swaps its content
 * between the model and texture viewers depending on the asset.
 */
public final class PreviewTabPane extends TabPane {
    private final ModelPresenter model;
    private final TexturePresenter texture;
    private final MetaPresenter meta;
    private final RawPresenter raw;

    private final Tab previewTab = new Tab("Preview");
    private final Tab metaTab = new Tab("Metadata");
    private final Tab rawTab = new Tab("Raw");

    @Inject
    PreviewTabPane(Feather feather) {
        this.model = feather.instance(ModelPresenter.class);
        this.texture = feather.instance(TexturePresenter.class);
        this.meta = feather.instance(MetaPresenter.class);
        this.raw = feather.instance(RawPresenter.class);

        for (Tab tab : new Tab[]{previewTab, metaTab, rawTab}) {
            tab.setClosable(false);
            tab.setDisable(true);
        }
        metaTab.setContent(meta.getFXNode());
        rawTab.setContent(raw.getFXNode());

        getTabs().setAll(previewTab, metaTab, rawTab);
    }

    /**
     * Decodes the viewers that apply to this asset. Pure CPU/IO work with no
     * scene-graph access, so it is safe to run off the FX thread; hand the
     * result to {@link #display} on the FX thread.
     */
    public PreviewData decode(AssetType type, Object assetData, Meta.Node metaNode) {
        Viewer renderer = switch (type) {
            case MODEL -> model;
            case TEXTURE -> texture;
            default -> null;
        };

        return new PreviewData(
            renderer,
            renderer != null ? renderer.decode(assetData) : null,
            metaNode != null ? meta.decode(metaNode) : null,
            raw.canPreview(type) ? raw.decode(assetData) : null);
    }

    public void display(PreviewData data) {
        showRenderer(data.renderer(), data.renderPayload());
        show(metaTab, meta, data.metaPayload());
        show(rawTab, raw, data.rawPayload());
        selectFirstEnabled();
    }

    private void showRenderer(Viewer renderer, Object payload) {
        if (renderer == null) {
            model.display(null);
            texture.display(null);
            previewTab.setContent(null);
            previewTab.setDisable(true);
            return;
        }

        // Release the renderer that isn't shown, then swap in the one that is.
        (renderer == model ? texture : model).display(null);
        previewTab.setContent(renderer.getFXNode());
        renderer.display(payload);
        previewTab.setDisable(false);
    }

    private void show(Tab tab, Viewer viewer, Object payload) {
        viewer.display(payload);
        tab.setDisable(payload == null);
    }

    private void selectFirstEnabled() {
        var selected = getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isDisable()) {
            return;
        }
        for (Tab tab : getTabs()) {
            if (!tab.isDisable()) {
                getSelectionModel().select(tab);
                return;
            }
        }
    }

    public record PreviewData(
        Viewer renderer,
        Object renderPayload,
        Object metaPayload,
        Object rawPayload
    ) {
    }
}
