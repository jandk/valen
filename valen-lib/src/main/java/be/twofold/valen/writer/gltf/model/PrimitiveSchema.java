package be.twofold.valen.writer.gltf.model;

import com.google.gson.*;

public record PrimitiveSchema(
    JsonObject attributes,
    int indices
) {
}
