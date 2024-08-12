package be.twofold.valen.core.game;

public interface AssetIdentifier extends Comparable<AssetIdentifier> {

    String pathName();

    String fileName();

    @Override
    default int compareTo(AssetIdentifier o) {
        var result = pathName().compareTo(o.pathName());
        if (result != 0) {
            return result;
        }
        return fileName().compareTo(o.fileName());
    }

}
