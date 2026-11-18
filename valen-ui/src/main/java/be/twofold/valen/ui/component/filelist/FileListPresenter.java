package be.twofold.valen.ui.component.filelist;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import jakarta.inject.*;

import java.util.*;
import java.util.stream.*;

public final class FileListPresenter extends AbstractPresenter<FileListView> implements FileListView.Listener {
    private Map<AssetPath, List<Asset>> assetIndex = Map.of();
    private Listener listener;

    @Inject
    FileListPresenter(FileListView view) {
        super(view);
        view.setListener(this);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onPathSelected(AssetPath path) {
        var assets = assetIndex.getOrDefault(path, List.of()).stream().sorted().toList();
        getView().setFilteredAssets(assets);
    }

    @Override
    public void onAssetSelected(Asset asset, boolean forced) {
        listener.onAssetSelected(asset, forced);
    }

    @Override
    public void onPathExportRequested(AssetPath path, boolean recursive) {
        listener.onPathExportRequested(path, recursive);
    }

    public void setAssets(Stream<? extends Asset> assets) {
        assetIndex = assets.collect(Collectors.groupingBy(asset -> asset.id().path().parent()));

        getView().setFileTree(buildPathTree());
    }

    private PathNode<PathCombo> buildPathTree() {
        var paths = new ArrayList<>(assetIndex.keySet());
        paths.sort(Comparator.naturalOrder());

        var root = new PathNode<>(new PathCombo(AssetPath.ROOT, "root"), true);
        for (var path : paths) {
            var segments = path.segments();

            var node = root;
            var current = AssetPath.ROOT;
            for (var i = 0; i < segments.size(); i++) {
                current = current.resolve(segments.get(i));
                node = node.get(new PathCombo(current, segments.get(i)), i == segments.size() - 1);
            }
        }
        return root;
    }

    public interface Listener {
        void onAssetSelected(Asset asset, boolean forced);

        void onPathExportRequested(AssetPath path, boolean recursive);
    }
}
