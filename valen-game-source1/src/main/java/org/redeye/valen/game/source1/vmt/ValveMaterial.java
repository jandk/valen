package org.redeye.valen.game.source1.vmt;

import org.redeye.valen.game.source1.utils.keyvalues.*;

public sealed interface ValveMaterial permits VertexLitGeneric {
    static ValveMaterial fromVdf(VdfValue value) {
        var object = value.asObject();
        var shaderType = object.keySet().stream().findFirst().orElseThrow();
        switch (shaderType) {
            case "vertexlitgeneric" -> {
                return new VertexLitGeneric(object.get(shaderType).asObject());
            }
            default -> {
                System.out.println("Unsupported shader type: " + object.keySet());
                return null;
            }
        }
    }
}
