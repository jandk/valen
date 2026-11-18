package be.twofold.valen.ui.component.preview;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.*;
import be.twofold.valen.ui.component.audioviewer.*;
import be.twofold.valen.ui.component.metaview.*;
import be.twofold.valen.ui.component.modelviewer.*;
import be.twofold.valen.ui.component.rawview.*;
import be.twofold.valen.ui.component.textureviewer.*;
import be.twofold.valen.ui.service.*;
import org.slf4j.*;
import wtf.reversed.toolbox.collect.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public final class PreviewPresenter extends AbstractPresenter<PreviewView> {
    private static final Logger log = LoggerFactory.getLogger(PreviewPresenter.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
        var thread = new Thread(runnable, "asset-loader");
        thread.setDaemon(true);
        return thread;
    });

    private final List<Viewer> renderers;
    private final MetaPresenter meta;
    private final RawPresenter raw;

    private final Session session;
    private final Settings settings;
    private final Dialogs dialogs;
    private final UiThread uiThread;

    // UI thread only
    private Asset asset;
    private boolean visible;
    private CompletableFuture<Preview> pending;

    public PreviewPresenter(
        PreviewView view,
        AudioPresenter audio,
        MetaPresenter meta,
        ModelPresenter model,
        RawPresenter raw,
        TexturePresenter texture,
        Session session,
        Settings settings,
        Dialogs dialogs,
        UiThread uiThread
    ) {
        super(view);

        this.renderers = List.of(audio, model, texture);
        this.meta = meta;
        this.raw = raw;

        this.session = session;
        this.settings = settings;
        this.dialogs = dialogs;
        this.uiThread = uiThread;

        view.setMetaView(meta.getView());
        view.setRawView(raw.getView());

        session.loaderProperty().subscribe(_ -> uiThread.execute(this::clear));
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
        load();
    }

    public void setVisible(boolean visible) {
        if (this.visible == visible) {
            return;
        }
        this.visible = visible;
        if (visible) {
            load();
        } else {
            cancel();
        }
    }

    public void reload() {
        load();
    }

    private void load() {
        cancelPending();

        var loader = session.getLoader();
        if (asset == null || !visible || loader == null) {
            getView().setLoading(false);
            return;
        }

        var asset = this.asset;
        var type = previewType(asset);
        var future = CompletableFuture.supplyAsync(() -> loadPreview(loader, asset, type), executor);
        this.pending = future;
        getView().setLoading(true);

        future.whenCompleteAsync((preview, error) -> {
            if (pending != future) {
                return;
            }
            pending = null;
            getView().setLoading(false);
            if (error != null) {
                var cause = error instanceof CompletionException ? error.getCause() : error;
                log.error("Could not load asset {}", asset.id().path().fileName(), cause);
                dialogs.showException("Could not load asset " + asset.id().path().fileName(), cause);
                return;
            }
            display(preview);
        }, uiThread);
    }

    private Preview loadPreview(AssetLoader loader, Asset asset, AssetType type) {
        try {
            var data = loader.load(asset.id(), type.type());
            var bytes = type == AssetType.RAW ? data : loader.load(asset.id(), Bytes.class);
            var metadata = type != AssetType.RAW
                ? loader.loadMetadata(asset.id()).orElse(null)
                : null;
            return decode(type, data, bytes, metadata);
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    private Preview decode(AssetType type, Object data, Object bytes, Meta.Node metadata) {
        var renderer = renderers.stream()
            .filter(r -> r.canPreview(type))
            .findFirst()
            .orElse(null);

        return new Preview(
            renderer,
            renderer != null ? renderer.decode(data) : null,
            metadata != null ? meta.decode(metadata) : null,
            raw.decode(bytes));
    }

    private void display(Preview preview) {
        // The other renderers get null, so they let go of what they hold (e.g. stop audio)
        for (var renderer : renderers) {
            renderer.display(renderer == preview.renderer() ? preview.renderPayload() : null);
        }
        meta.display(preview.metaPayload());
        raw.display(preview.rawPayload());

        getView().show(
            preview.renderer() != null ? preview.renderer().getView() : null,
            preview.metaPayload() != null,
            preview.rawPayload() != null);
    }

    private AssetType previewType(Asset asset) {
        if (settings.isTreatAsRaw()) {
            return AssetType.RAW;
        }
        return switch (asset.type()) {
            case AUDIO, MODEL, TEXTURE -> asset.type();
            default -> AssetType.RAW;
        };
    }

    private void cancelPending() {
        if (pending != null) {
            pending.cancel(false);
            pending = null;
        }
    }

    private void cancel() {
        cancelPending();
        getView().setLoading(false);
    }

    private void clear() {
        asset = null;
        cancel();
        display(Preview.EMPTY);
    }

    private record Preview(
        Viewer renderer,
        Object renderPayload,
        Object metaPayload,
        Object rawPayload
    ) {
        static final Preview EMPTY = new Preview(null, null, null, null);
    }
}
