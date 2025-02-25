package be.twofold.valen.core.game;

import be.twofold.valen.core.util.*;

public interface AssetID extends Comparable<AssetID> {

    String fullName();

    default String displayName() {
        return fileName();
    }

    default String pathName() {
        return Filenames.pathName(fullName());
    }

    default String fileName() {
        return Filenames.fileName(fullName());
    }

    @Override
    default int compareTo(AssetID o) {
        return fullName().compareTo(o.fullName());
    }

}
