package be.twofold.valen.game.goldsrc.reader.mdl.v10;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record StudioTriVert(
    short vertIndex,
    short normIndex,
    short s,
    short t
) {
    public static StudioTriVert read(BinarySource source) throws IOException {
        var vertIndex = source.readShort();
        var normIndex = source.readShort();
        var s = source.readShort();
        var t = source.readShort();

        return new StudioTriVert(
            vertIndex,
            normIndex,
            s,
            t
        );
    }
}
