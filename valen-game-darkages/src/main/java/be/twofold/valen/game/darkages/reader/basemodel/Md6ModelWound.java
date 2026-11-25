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
    public static Md6ModelWound read(BinaryReader reader) throws IOException {
        var name = reader.readString(StringFormat.INT_LENGTH);
        var unknown = Vector4.read(reader);
        var meshWounds = reader.readObjects(reader.readInt(), Md6ModelMeshWound::read);

        return new Md6ModelWound(
            name,
            unknown,
            meshWounds
        );
    }
}
