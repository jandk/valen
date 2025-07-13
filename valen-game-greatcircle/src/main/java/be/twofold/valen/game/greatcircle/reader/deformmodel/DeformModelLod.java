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
    public static DeformModelLod read(BinaryReader reader) throws IOException {
        byte unknown1 = reader.readByte();
        int version = reader.readInt();
        int numVertices;
        if (version < 0) {
            numVertices = reader.readInt();
            version = -version;
        } else {
            numVertices = version;
        }
        if (version != 3) {
            throw new IOException("Unsupported version: " + version);
        }

        int numIndices = reader.readInt();
        float unknown2 = reader.readFloat();
        int vertexMask = reader.readInt();
        Bounds bounds = Bounds.read(reader);
        Vector3 vertexOffset = Vector3.read(reader);
        float vertexScale = reader.readFloat();
        Vector2 uvOffset = Vector2.read(reader);
        float uvScale = reader.readFloat();
        int unknown3 = reader.readInt();
        float unknown4 = reader.readFloat();
        float unknown5 = reader.readFloat();
        String materialName = reader.readPString();
        List<Entry> entries = reader.readObjects(reader.readInt(), Entry::read);

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
        public static Entry read(BinaryReader reader) throws IOException {
            var name = reader.readPString();
            var unknown1 = reader.readInt();
            var unknown2 = reader.readInt();
            return new Entry(name, unknown1, unknown2);
        }
    }
}
