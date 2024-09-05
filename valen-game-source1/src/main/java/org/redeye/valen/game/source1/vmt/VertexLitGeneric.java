package org.redeye.valen.game.source1.vmt;

import org.redeye.valen.game.source1.utils.keyvalues.*;

public record VertexLitGeneric(VdfValue.VdfObject data) implements ValveMaterial {

    String colorTexture() {
        if (data.has("$basetexture")) {
            return data.get("$basetexture").asArray().get(0).asString();
        }
        return null;
    }

    String normalTexture() {
        if (data.has("$bumpmap")) {
            return data.get("$bumpmap").asArray().get(0).asString();
        }
        return null;
    }

}
