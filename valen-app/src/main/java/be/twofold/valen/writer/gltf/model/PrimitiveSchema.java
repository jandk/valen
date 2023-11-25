package be.twofold.valen.writer.gltf.model;

import com.fasterxml.jackson.databind.node.*;

public record PrimitiveSchema(
    ObjectNode attributes,
    int indices
) {
}
