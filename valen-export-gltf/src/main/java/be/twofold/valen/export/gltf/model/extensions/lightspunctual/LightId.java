package be.twofold.valen.export.gltf.model.extensions.lightspunctual;

import be.twofold.valen.export.gltf.model.*;

public final class LightId extends AbstractId {
    private LightId(int id) {
        super(id);
    }

    public static LightId of(int id) {
        return new LightId(id);
    }
}
