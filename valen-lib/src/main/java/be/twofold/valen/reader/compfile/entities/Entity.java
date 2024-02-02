package be.twofold.valen.reader.compfile.entities;

import com.google.gson.*;

import java.util.*;

public record Entity(
    List<String> layers,
    Integer instanceId,
    String originalName,
    JsonObject entityDef
) {
}
