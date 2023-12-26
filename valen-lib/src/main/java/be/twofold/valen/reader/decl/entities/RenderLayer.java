package be.twofold.valen.reader.decl.entities;

import com.google.gson.*;

public record RenderLayer(
    String decl,
    JsonObject parms
) {
}
