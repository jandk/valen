package be.twofold.valen.middleware.wwise.shared;

import be.twofold.valen.core.game.*;

public record WwiseAssetId(
    MediaId id,
    String name
) implements AssetID {
    @Override
    public String fullName() {
        if (name != null) {
            return name;
        }
        return id + ".wem";
    }
}
