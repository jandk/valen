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

    private PathNode<String> buildPathTree() {
        var paths = new ArrayList<>(assetIndex.keySet());
        paths.sort(Comparator.naturalOrder());

        var root = new PathNode<>("root", true);
        for (var path : paths) {
            if (path.isBlank()) {
                continue;
            }

            var node = root;
            var split = path.split("/");
            for (var i = 0; i < split.length; i++) {
                var hasFiles = i == split.length - 1;
                node = node.get(split[i], hasFiles);
            }
        }
        return root;
    }
}
