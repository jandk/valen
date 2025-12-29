package be.twofold.valen.game.goldsrc.reader.mdl.v10;

import be.twofold.valen.core.math.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record StudioBone(
    String name,
    int parent,
    int flags,
    Ints boneController,
    Vector3 pos,
    Vector3 rot,
    Vector3 posScale,
    Vector3 rotScale
) {

    public static StudioBone read(BinarySource source) throws IOException {
        var name = source.readString(32).trim();
        var parent = source.readInt();
        var flags = source.readInt();
        var boneController = source.readInts(6);
        var pos = Vector3.read(source);
        var rot = Vector3.read(source);
        var posScale = Vector3.read(source);
        var rotScale = Vector3.read(source);
        return new StudioBone(name, parent, flags, boneController, pos, rot, posScale, rotScale);
    }
}
