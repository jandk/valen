package org.redeye.valen.game.spacemarines2.psSection;

import be.twofold.valen.core.io.*;
import com.google.gson.*;

import java.io.*;

public class PsSectionBinary {
    private final DataSource source;

    public static JsonObject parseFromDataSource(DataSource source) throws IOException {
        return new PsSectionBinary(source).parse();
    }

    private PsSectionBinary(DataSource source) {
        this.source = source;
    }

    public JsonObject parse() throws IOException {
        return readObject();
    }

    private JsonObject readObject() throws IOException {
        var count = source.readInt();
        var obj = new JsonObject();
        for (int i = 0; i < count; i++) {
            var name = source.readPString();
            obj.add(name, readValue());
        }
        return obj;
    }

    private JsonArray readArray() throws IOException {
        var count = source.readInt();
        var arr = new JsonArray(count);
        for (int i = 0; i < count; i++) {
            arr.add(readValue());
        }
        return arr;
    }

    private JsonElement readValue() throws IOException {
        var type = source.readInt();
        return switch (type) {
            case 1 -> new JsonPrimitive(source.readInt());
            case 2 -> new JsonPrimitive(source.readFloat());
            case 3 -> new JsonPrimitive(source.readBoolByte());
            case 4 -> new JsonPrimitive(source.readPString());
            case 6 -> readArray();
            case 7 -> readObject();
            default -> throw new IOException("Unimplemented type: " + type);
        };
    }

}
