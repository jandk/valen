package org.redeye.valen.game.halflife.mdl.v10;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record StudioBodyPart(String name, int base, List<StudioModel> models) {

    public static StudioBodyPart read(DataSource source) throws IOException {
        var name = source.readString(64).trim();
        var count = source.readInt();
        var base = source.readInt();
        var offset = source.readInt();
        var meshes = new ArrayList<StudioModel>();
        for (long i = 0; i < count; i++) {
            source.seek(offset + i * 112);
            meshes.add(StudioModel.read(source));
        }

        return new StudioBodyPart(name, base, meshes);
    }

}
