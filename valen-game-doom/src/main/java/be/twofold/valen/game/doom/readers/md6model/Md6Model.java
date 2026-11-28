package be.twofold.valen.game.doom.readers.md6model;

import be.twofold.valen.game.doom.readers.common.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Md6Model(
    Md6ModelHeader header,
    List<Md6Mesh> meshes,
    List<SourceSurface> sourceSurfaces
) {
    public static final int MAGIC = 0x2B02_4D4D;

    public static Md6Model read(BinaryReader reader) throws IOException {
        var header = Md6ModelHeader.read(reader);
        var meshes = reader.readObjects(reader.readInt(), Md6Mesh::read);
        var sourceSurfaces = reader.readObjects(reader.readInt(), SourceSurface::read);
        reader.expectInt(MAGIC);

        return new Md6Model(
            header,
            meshes,
            sourceSurfaces
        );
    }
}
