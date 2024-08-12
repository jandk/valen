package be.twofold.valen.game.eternal.reader.filecompressed.entities;

import com.google.gson.*;

import java.util.*;

public record Entity(
    List<String> layers,
    Integer instanceId,
    String originalName,
    JsonObject entityDef
) {
}
