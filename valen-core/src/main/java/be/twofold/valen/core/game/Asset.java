package be.twofold.valen.core.game;

import be.twofold.valen.core.game.io.*;

import java.util.*;

public interface Asset extends Comparable<Asset> {

    AssetID id();

    AssetType type();

    Location location();

    int size();

    Map<String, Object> properties();

    default String exportName() {
        return id().fileNameWithoutExtension();
    }

    @Override
    default int compareTo(Asset o) {
        return id().compareTo(o.id());
    }

}
