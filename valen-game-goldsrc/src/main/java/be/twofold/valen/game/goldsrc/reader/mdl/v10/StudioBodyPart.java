package be.twofold.valen.game.goldsrc.reader.mdl.v10;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record StudioBodyPart(
    String name,
    int base,
    List<StudioModel> models
) {
    public static StudioBodyPart read(BinarySource source) throws IOException {
        var name = source.readString(64).trim();
        var count = source.readInt();
        var base = source.readInt();
        var offset = source.readInt();
        var meshes = new ArrayList<StudioModel>();
        for (long i = 0; i < count; i++) {
            source.position(offset + i * 112);
            meshes.add(StudioModel.read(source));
        }
        return new StudioBodyPart(name, base, meshes);
    }
}
