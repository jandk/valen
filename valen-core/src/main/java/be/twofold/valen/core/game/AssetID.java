package be.twofold.valen.core.game;

import be.twofold.valen.core.util.*;

public interface AssetID extends Comparable<AssetID> {

    String fullName();

    default String displayName() {
        return fileName();
    }

    default String pathName() {
        return Filenames.getPath(fullName());
    }

    default String fileName() {
        return Filenames.getName(fullName());
    }

    default String fileNameWithoutExtension() {
        return Filenames.removeExtension(fileName());
    }

    default String extension() {
        return Filenames.getExtension(fileName());
    }

    @Override
    default int compareTo(AssetID o) {
        return fullName().compareTo(o.fullName());
    }

}
