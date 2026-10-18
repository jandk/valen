package be.twofold.valen.core.game;

public interface AssetID extends Comparable<AssetID> {

    String fullName();

    default AssetPath path() {
        return AssetPath.of(fullName());
    }

    default String displayName() {
        return path().fileName();
    }

    @Override
    default int compareTo(AssetID o) {
        return fullName().compareTo(o.fullName());
    }

}
