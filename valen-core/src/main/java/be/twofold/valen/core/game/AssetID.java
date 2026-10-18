package be.twofold.valen.core.game;

public interface AssetID extends Comparable<AssetID> {

    String fullName();

    default AssetPath path() {
        return new AssetPath(fullName());
    }

    default String displayName() {
        return fileName();
    }

    default String fileName() {
        return path().fileName();
    }

    default String pathName() {
        return path().parent().toString();
    }

    default String extension() {
        return path().extension();
    }

    default String fileNameWithoutExtension() {
        return path().baseName();
    }

    @Override
    default int compareTo(AssetID o) {
        return fullName().compareTo(o.fullName());
    }

}
