package be.twofold.valen.game.source;

import be.twofold.valen.core.game.*;

public record SourceAssetID(
    String name
) implements AssetID {
    @Override
    public String fullName() {
        return name;
    }

    public String extension() {
        var index = name.lastIndexOf('.');
        return index == -1 ? name : name.substring(index + 1);
    }
}
