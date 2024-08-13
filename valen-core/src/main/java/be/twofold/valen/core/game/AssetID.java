package be.twofold.valen.core.game;

public interface AssetID extends Comparable<AssetID> {

    String fullName();

    String pathName();

    String fileName();

    @Override
    default int compareTo(AssetID o) {
        var result = pathName().compareTo(o.pathName());
        if (result != 0) {
            return result;
        }
        return fileName().compareTo(o.fileName());
    }

}
