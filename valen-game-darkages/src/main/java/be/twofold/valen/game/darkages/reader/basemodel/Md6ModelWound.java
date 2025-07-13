package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.BinaryReader;
import be.twofold.valen.core.math.*;

import java.io.*;
import java.util.*;

public record Md6ModelWound(
    String name,
    Vector4 unknown,
    List<Md6ModelMeshWound> meshWounds
) {
    public static Md6ModelWound read(BinaryReader reader) throws IOException {
        var name = reader.readPString();
        var unknown = Vector4.read(reader);
        var meshWounds = reader.readObjects(reader.readInt(), Md6ModelMeshWound::read);

        return new Md6ModelWound(
            name,
            unknown,
            meshWounds
        );
    }
}
