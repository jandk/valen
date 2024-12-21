package be.twofold.valen.ui.component.filelist;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.event.*;
import be.twofold.valen.ui.events.*;
import jakarta.inject.*;

import java.util.*;
import java.util.stream.*;

public final class FileListPresenter extends AbstractFXPresenter<FileListView> {
    private final SendChannel<AssetSelected> channel;
    private Map<String, List<Asset>> assetIndex = Map.of();

    @Inject
    FileListPresenter(FileListView view, EventBus eventBus) {
        super(view);

        this.channel = eventBus.senderFor(AssetSelected.class);

        eventBus
            .receiverFor(FileListViewEvent.class)
            .consume(event -> {
                switch (event) {
                    case FileListViewEvent.AssetSelected assetSelected ->
                        channel.send(new AssetSelected(assetSelected.asset()));
                    case FileListViewEvent.PathSelected pathSelected -> selectPath(pathSelected.path());
                }
            });
    }

    public Asset getSelectedAsset() {
        return getView().getSelectedAsset();
    }

    public void setAssets(List<Asset> assets) {
        assetIndex = assets.stream()
            .collect(Collectors.groupingBy(asset -> asset.id().pathName()));

        getView().setFileTree(buildPathTree());
    }

    private void selectPath(String path) {
        var assets = assetIndex.getOrDefault(path, List.of());
        getView().setFilteredAssets(assets);
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