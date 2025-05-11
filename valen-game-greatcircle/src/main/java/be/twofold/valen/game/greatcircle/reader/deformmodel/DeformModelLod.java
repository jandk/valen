package be.twofold.valen.game.greatcircle.reader.deformmodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;
import java.util.*;

public record DeformModelLod(
    byte unknown1,
    int numVertices,
    int numIndices,
    float unknown2,
    int vertexMask,
    Bounds bounds,
    Vector3 vertexOffset,
    float vertexScale,
    Vector2 uvOffset,
    float uvScale,
    int unknown3,
    float unknown4,
    float unknown5,
    String materialName,
    List<Entry> entries
) implements LodInfo {
    public static DeformModelLod read(DataSource source) throws IOException {
        byte unknown1 = source.readByte();
        int version = source.readInt();
        int numVertices;
        if (version < 0) {
            numVertices = source.readInt();
            version = -version;
        } else {
            numVertices = version;
        }
        if (version != 3) {
            throw new IOException("Unsupported version: " + version);
        }

        int numIndices = source.readInt();
        float unknown2 = source.readFloat();
        int vertexMask = source.readInt();
        Bounds bounds = Bounds.read(source);
        Vector3 vertexOffset = Vector3.read(source);
        float vertexScale = source.readFloat();
        Vector2 uvOffset = Vector2.read(source);
        float uvScale = source.readFloat();
        int unknown3 = source.readInt();
        float unknown4 = source.readFloat();
        float unknown5 = source.readFloat();
        String materialName = source.readPString();
        List<Entry> entries = source.readObjects(source.readInt(), Entry::read);

        return new DeformModelLod(
            unknown1,
            numVertices,
            numIndices,
            unknown2,
            vertexMask,
            bounds,
            vertexOffset,
            vertexScale,
            uvOffset,
            uvScale,
            unknown3,
            unknown4,
            unknown5,
            materialName,
            entries
        );
    }

    @Override
    public int numFaces() {
        if (numIndices % 3 != 0) {
            throw new IllegalArgumentException("numIndices must be a multiple of 3");
        }
        return numIndices / 3;
    }

    public record Entry(
        String name,
        int unknown1,
        int unknown2
    ) {
        public static Entry read(DataSource source) throws IOException {
            var name = source.readPString();
            var unknown1 = source.readInt();
            var unknown2 = source.readInt();
            return new Entry(name, unknown1, unknown2);
        }
    }
}
