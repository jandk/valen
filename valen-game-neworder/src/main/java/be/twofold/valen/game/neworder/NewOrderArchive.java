package be.twofold.valen.game.neworder;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class NewOrderArchive implements Archive {
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
