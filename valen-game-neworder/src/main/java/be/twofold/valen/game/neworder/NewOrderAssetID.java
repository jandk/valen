package be.twofold.valen.game.neworder;

import be.twofold.valen.core.game.*;

public record NewOrderAssetID(
    String typeName,
    String resourceName
) implements AssetID {
    @Override
    public String fullName() {
        return resourceName;
    }

    @Override
    public String pathName() {
        var index = resourceName.lastIndexOf('/');
        return index == -1 ? "" : resourceName.substring(0, index);
    }

    @Override
    public String fileName() {
        var index = resourceName.lastIndexOf('/');
        return index == -1 ? resourceName : resourceName.substring(index + 1);
    }
}
