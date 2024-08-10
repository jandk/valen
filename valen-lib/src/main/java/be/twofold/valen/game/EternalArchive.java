package be.twofold.valen.game;

import be.twofold.valen.core.game.*;
import be.twofold.valen.resource.*;

import java.io.*;
import java.util.*;

public final class EternalArchive implements Archive<ResourceKey> {
    private final StreamDbCollection streamDbCollection;
    private final ResourcesCollection resourcesCollection;

    public EternalArchive(StreamDbCollection streamDbCollection, ResourcesCollection resourcesCollection) {
        this.streamDbCollection = Objects.requireNonNull(streamDbCollection);
        this.resourcesCollection = Objects.requireNonNull(resourcesCollection);
    }

    @Override
    public List<Asset<ResourceKey>> assets() {
        return resourcesCollection.getEntries().stream()
            .map(entry -> new Asset<>(entry.key(), mapType(entry.type())))
            .distinct()
            .sorted()
            .toList();
    }

    @Override
    public Object loadAsset(Asset<ResourceKey> asset) throws IOException {
        var resource = resourcesCollection.get(asset.identifier())
            .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + asset.identifier()));

        return resourcesCollection.read(resource);
    }

    private AssetType mapType(ResourceType type) {
        return AssetType.Binary;
    }
}
