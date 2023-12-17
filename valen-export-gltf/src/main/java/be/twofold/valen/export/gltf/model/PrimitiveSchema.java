package be.twofold.valen.export.gltf.model;

import com.google.gson.*;

public record PrimitiveSchema(
    JsonObject attributes,
    int indices
) {
}
