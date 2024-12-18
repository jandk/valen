package be.twofold.valen.core.game;

import be.twofold.valen.core.util.*;

public interface AssetID extends Comparable<AssetID> {

    String fullName();

    String displayName();

    default String pathName() {
        return Filenames.pathName(fullName());
    }

    default String fileName() {
        return Filenames.fileName(fullName());
    }

    @Override
    default int compareTo(AssetID o) {
        var result = pathName().compareTo(o.pathName());
        if (result != 0) {
            return result;
        }
        return fileName().compareTo(o.fileName());
    }

}
