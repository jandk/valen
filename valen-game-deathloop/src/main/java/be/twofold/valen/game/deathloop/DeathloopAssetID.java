package be.twofold.valen.game.deathloop;

import be.twofold.valen.core.game.*;

public record DeathloopAssetID(
    String name
) implements AssetID {
    @Override
    public String fullName() {
        return name;
    }

    @Override
    public String pathName() {
        var index = name.lastIndexOf('/');
        return index == -1 ? "" : name.substring(0, index);
    }

    @Override
    public String fileName() {
        var index = name.lastIndexOf('/');
        return index == -1 ? name : name.substring(index + 1);
    }
}
