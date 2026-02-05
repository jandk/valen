package be.twofold.valen.core.game;

import java.util.*;

public interface Asset extends Comparable<Asset> {

    AssetID id();

    AssetType type();

    StorageLocation location();

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
