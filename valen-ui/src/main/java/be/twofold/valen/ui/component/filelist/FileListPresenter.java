package be.twofold.valen.ui.component.filelist;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.service.*;
import jakarta.inject.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class FileListPresenter extends AbstractPresenter<FileListView> implements FileListView.Listener {
    private final Session session;
    private final Settings settings;
    private final ExportService exportService;

    private Map<AssetPath, List<Asset>> assetIndex = Map.of();
    private String query = "";
    private Listener listener;

    @Inject
    FileListPresenter(FileListView view, Session session, Settings settings, ExportService exportService, UiThread uiThread) {
        super(view);
        this.session = session;
        this.settings = settings;
        this.exportService = exportService;

        view.setListener(this);
        session.loaderProperty().subscribe(_ -> uiThread.execute(this::refresh));
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setQuery(String query) {
        this.query = query;
        refresh();
    }

    public void refresh() {
        assetIndex = filteredAssets()
            .collect(Collectors.groupingBy(asset -> asset.id().path().parent()));

        getView().setFileTree(buildPathTree());
    }

    public void exportSelected() {
        export(getView().getSelectedAssets());
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
        var assets = assetIndex.entrySet().stream()
            .filter(e -> recursive ? e.getKey().startsWith(path) : e.getKey().equals(path))
            .flatMap(e -> e.getValue().stream())
            .toList();

        export(assets);
    }

    private Stream<? extends Asset> filteredAssets() {
        var loader = session.getLoader();
        if (loader == null) {
            return Stream.empty();
        }

        var assetTypes = settings.isTreatAsRaw() ? Set.<AssetType>of() : settings.getAssetTypes();
        var predicate = buildPredicate(query, assetTypes);
        return loader.all().filter(predicate);
    }

    private Predicate<Asset> buildPredicate(String assetName, Collection<AssetType> assetTypes) {
        var predicates = new ArrayList<Predicate<Asset>>();
        if (assetName != null) {
            predicates.add(asset -> asset.id().fullName().contains(assetName));
        }
        if (assetTypes != null && !assetTypes.isEmpty()) {
            var assetTypeSet = EnumSet.copyOf(assetTypes);
            predicates.add(asset -> assetTypeSet.contains(asset.type()));
        }
        return predicates.stream()
            .reduce(Predicate::and)
            .orElse(_ -> true);
    }

    private void export(List<? extends Asset> assets) {
        var loader = session.getLoader();
        if (loader == null || assets.isEmpty()) {
            return;
        }
        exportService.export(loader, assets);
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
    }
}
