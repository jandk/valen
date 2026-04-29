package be.twofold.valen.game.darkages.reader.basemodel;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.util.*;

public record Md6ModelWound(
    String name,
    Vector4 unknown,
    List<Md6ModelMeshWound> meshWounds
) {
    public static Md6ModelWound read(BinarySource source) throws IOException {
        var name = source.readString(StringFormat.INT_LENGTH);
        var unknown = Vector4.read(source);
        var meshWounds = source.readObjects(source.readInt(), Md6ModelMeshWound::read);

        return new Md6ModelWound(
            name,
            unknown,
            meshWounds
        );
    }
}
