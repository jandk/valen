package org.redeye.valen.game.halflife2.providers;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

public class VpkArchive implements Provider {
    private final Path path;

    public VpkArchive(Path path) {
        this.path = path;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public Provider getParent() {
        return null;
    }

    @Override
    public List<Asset> assets() {
        return List.of();
    }

    @Override
    public boolean exists(AssetID identifier) {
        return false;
    }

    @Override
    public Object loadAsset(AssetID identifier) throws IOException {
        return null;
    }

    @Override
    public ByteBuffer loadRawAsset(AssetID identifier) throws IOException {
        return null;
    }
}
