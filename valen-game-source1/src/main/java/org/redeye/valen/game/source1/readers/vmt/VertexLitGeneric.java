package org.redeye.valen.game.source1.readers.vmt;

import org.redeye.valen.game.source1.readers.keyvalue.*;

import java.util.*;

public record VertexLitGeneric(KeyValue.Obj object) implements ValveMaterial {
    Optional<String> colorTexture() {
        List<KeyValue> values = object.get("$basetexture");
        if (values.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(((KeyValue.Str) values.getFirst()).value());
    }

    Optional<String> normalTexture() {
        List<KeyValue> values = object.get("$bumpmap");
        if (values.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(((KeyValue.Str) values.getFirst()).value());
    }
}
