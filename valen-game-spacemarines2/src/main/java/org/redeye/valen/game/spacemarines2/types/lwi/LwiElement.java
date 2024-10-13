package org.redeye.valen.game.spacemarines2.types.lwi;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record LwiElement(
    String name,
    String type,
    byte createCollisionActor,
    String tplName,
    List<MaterialReplacement> materialReplacements,
    Long typeId
) {
    public static LwiElement read(DataSource source, int version) throws IOException {
        String name = source.readPString();
        String type = null;
        byte createCollisionActor = 0;
        String tplName = null;
        List<MaterialReplacement> materialReplacements = new ArrayList<>();
        long typeId = 0L;
        if (version >= 8) {
            type = source.readPString();
            createCollisionActor = source.readByte();
            tplName = source.readPString();
            var count = source.readInt();
            for (int i = 0; i < count; i++) {
                materialReplacements.add(MaterialReplacement.read(source));
            }
        }
        if (version > 8) {
            typeId = source.readLong();
        }

        return new LwiElement(name, type, createCollisionActor, tplName, materialReplacements, typeId);
    }
}
