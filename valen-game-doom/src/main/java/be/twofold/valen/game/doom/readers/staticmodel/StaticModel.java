package be.twofold.valen.game.doom.readers.staticmodel;

import be.twofold.valen.game.doom.readers.common.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public record StaticModel(
    StaticModelHeader header,
    List<StaticModelSurface> modelSurfaces,
    List<SourceSurface> sourceSurfaces
) {
    public static final int MAGIC = 'L' << 16 | 'M' << 8 | 'B';

    public static StaticModel read(BinaryReader reader) throws IOException {
        reader.order(ByteOrder.BIG_ENDIAN);
        var header = StaticModelHeader.read(reader);
        var modelSurfaces = reader.readObjects(header.numSurfaces(), r -> StaticModelSurface.read(r, header.version()));
        var sourceSurfaces = reader.readObjects(reader.readInt(), SourceSurface::read);

        return new StaticModel(
            header,
            modelSurfaces,
            sourceSurfaces
        );
    }
}
