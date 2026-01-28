package be.twofold.valen.ui.component.filelist;

import backbonefx.event.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.events.*;
import jakarta.inject.*;

import java.util.*;
import java.util.stream.*;

public final class FileListPresenter extends AbstractPresenter<FileListView> implements FileListViewListener {

    private Map<String, List<Asset>> assetIndex = Map.of();
    private final EventBus eventBus;

    @Inject
    FileListPresenter(FileListView view, EventBus eventBus) {
        super(view);
        this.eventBus = eventBus;

        view.setListener(this);
    }

    @Override
    public void onPathSelected(String path) {
        var assets = assetIndex.getOrDefault(path, List.of()).stream().sorted().toList();
        getView().setFilteredAssets(assets);
    }

    @Override
    public void onAssetSelected(Asset asset, boolean forced) {
        eventBus.publish(new AssetSelected(asset, forced));
    }

    @Override
    public void onPathExportRequested(String path, boolean recursive) {
        eventBus.publish(new ExportRequested(path, recursive));
    }

    public void setAssets(Stream<? extends Asset> assets) {
        assetIndex = assets.collect(Collectors.groupingBy(asset -> asset.id().pathName()));

        getView().setFileTree(buildPathTree());
    }

    private PathNode<PathCombo> buildPathTree() {
        var paths = new ArrayList<>(assetIndex.keySet());
        paths.sort(Comparator.naturalOrder());

        var root = new PathNode<>(new PathCombo("", "root"), true);
        for (var path : paths) {
            if (path.isBlank()) {
                continue;
            }

            var indices = new ArrayList<Integer>();
            indices.add(-1);
            for (int i = 0; i < path.length(); i++) {
                if (path.charAt(i) == '/') {
                    indices.add(i);
                }
            }
            indices.add(path.length());

            var node = root;
            for (var i = 0; i < indices.size() - 1; i++) {
                var part = path.substring(indices.get(i) + 1, indices.get(i + 1));
                node = node.get(
                    new PathCombo(path.substring(0, indices.get(i + 1)), part),
                    i == indices.size() - 2
                );
            }
        }
        return root;
    }
}
