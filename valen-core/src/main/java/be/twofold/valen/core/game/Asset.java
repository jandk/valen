package be.twofold.valen.core.game;

import java.util.*;

public interface Asset extends Comparable<Asset> {

    AssetID id();

    AssetType<?> type();

    int size();

    Map<String, Object> properties();

    @Override
    default int compareTo(Asset o) {
        return id().compareTo(o.id());
    }

}
