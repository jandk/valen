package org.redeye.valen.game.spacemarines2.psSection;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public class PsSectionBinary {
    private final DataSource source;

    public static PsSectionValue.PsSectionObject parseFromDataSource(DataSource source) throws IOException {
        return new PsSectionBinary(source).parse();
    }

    private PsSectionBinary(DataSource source) {
        this.source = source;
    }

    public PsSectionValue.PsSectionObject parse() throws IOException {
        return readObject();
    }

    private PsSectionValue.PsSectionObject readObject() throws IOException {
        var count = source.readInt();
        var map = new LinkedHashMap<String, PsSectionValue>();
        for (int i = 0; i < count; i++) {
            var name = source.readPString();
            var bin = readValue();
            map.put(name, bin);
        }
        return new PsSectionValue.PsSectionObject(map);
    }

    private PsSectionValue.PsSectionList readArray() throws IOException {
        var count = source.readInt();
        var arr = new ArrayList<PsSectionValue>(count);
        for (int i = 0; i < count; i++) {
            arr.add(readValue());
        }
        return new PsSectionValue.PsSectionList(arr);
    }

    private PsSectionValue readValue() throws IOException {
        var type = source.readInt();
        return switch (type) {
            case 1 -> new PsSectionValue.PsSectionNumber(source.readInt());
            case 2 -> new PsSectionValue.PsSectionNumber(source.readFloat());
            case 3 -> new PsSectionValue.PsSectionBoolean(source.readBoolByte());
            case 4 -> new PsSectionValue.PsSectionString(source.readPString());
            case 6 -> readArray();
            case 7 -> readObject();
            default -> throw new IOException("Unimplemented type: " + type);
        };
    }

}
