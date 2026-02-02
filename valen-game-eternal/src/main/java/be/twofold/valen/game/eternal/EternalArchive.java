package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.resource.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class EternalArchive implements Archive {
    private final Map<AssetID, Asset> loadedIndex;
    private final Map<AssetID, Asset> commonIndex;

    EternalArchive(
        List<ResourcesFile> loadedFiles,
        List<ResourcesFile> commonFiles
    ) {
        this.loadedIndex = buildIndex(loadedFiles);
        this.commonIndex = buildIndex(commonFiles);
    }

    private Map<AssetID, Asset> buildIndex(List<ResourcesFile> files) {
        return files.stream()
            .flatMap(ResourcesFile::getAll)
            .collect(Collectors.toUnmodifiableMap(
                EternalAsset::id,
                Function.identity(),
                (first, _) -> first
            ));
    }

    @Override
    public Optional<Asset> get(AssetID id) {
        Asset asset = loadedIndex.get(id);
        if (asset == null) {
            asset = commonIndex.get(id);
        }
        return Optional.ofNullable(asset);
    }

    @Override
    public Stream<Asset> all() {
        return loadedIndex.values().stream();
    }
}
