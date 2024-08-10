package be.twofold.valen.core.game;

public interface AssetIdentifier extends Comparable<AssetIdentifier> {

    String fileName();

    String pathName();

    @Override
    default int compareTo(AssetIdentifier o) {
        int result = pathName().compareTo(o.pathName());
        if (result != 0) {
            return result;
        }

        return fileName().compareTo(o.fileName());
    }

}
