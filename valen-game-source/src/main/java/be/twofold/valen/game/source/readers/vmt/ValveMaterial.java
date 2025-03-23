package be.twofold.valen.game.source.readers.vmt;

import be.twofold.valen.game.source.readers.keyvalue.*;

public sealed interface ValveMaterial permits VertexLitGeneric {
    static ValveMaterial read(KeyValue.Obj obj) {
        var shaderType = obj.values().stream().findFirst().orElseThrow().getKey();
        return switch (shaderType) {
            case "vertexlitgeneric" -> new VertexLitGeneric(obj.getObject(shaderType));
            default -> throw new UnsupportedOperationException(shaderType + " not supported");
        };
    }
}
