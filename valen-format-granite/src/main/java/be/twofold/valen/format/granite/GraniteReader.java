package be.twofold.valen.format.granite;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;

import java.io.*;
import java.util.*;

public final class GraniteReader implements AssetReader<Texture, Asset> {
    private final Map<String, GraniteContainer> graniteContainers;

    public GraniteReader(Map<String, GraniteContainer> graniteContainers) {
        this.graniteContainers = Map.copyOf(graniteContainers);
    }

    @Override
    public boolean canRead(Asset asset) {
        return asset instanceof GraniteAsset;
    }

    @Override
    public Texture read(Asset asset, LoadingContext context) throws IOException {
        if (!(asset instanceof GraniteAsset graniteAsset)) {
            throw new IllegalArgumentException("Granite asset locations must implement GraniteLocation");
        }

        return graniteContainers
            .get(graniteAsset.container())
            .read(graniteAsset.texture(), graniteAsset.layer());
    }
}
