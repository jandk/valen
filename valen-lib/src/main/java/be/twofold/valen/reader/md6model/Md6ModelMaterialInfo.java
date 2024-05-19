package be.twofold.valen.reader.md6model;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Md6ModelMaterialInfo(String name, int meshId, int vertexFrom, int vertexTo) {
    public static Md6ModelMaterialInfo read(DataSource source) throws IOException {
        var name = source.readPString();
        var meshId = source.readInt();
        var vertexFrom = source.readInt();
        var vertexTo = source.readInt();
        return new Md6ModelMaterialInfo(name, meshId, vertexFrom, vertexTo);
    }
}
