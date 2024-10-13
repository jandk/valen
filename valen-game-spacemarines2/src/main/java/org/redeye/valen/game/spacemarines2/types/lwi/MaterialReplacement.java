package org.redeye.valen.game.spacemarines2.types.lwi;

import be.twofold.valen.core.io.*;

import java.io.*;

public record MaterialReplacement(
    String textureName,
    String sourceMaterialName,
    String targetMaterialName,
    String objName,
    byte name_
) {
    public static MaterialReplacement read(DataSource source) throws IOException {
        return new MaterialReplacement(source.readPString(), source.readPString(), source.readPString(), source.readPString(), source.readByte());
    }
}
