package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;
import java.util.*;

public record Md6ModelWound(
    String name,
    Vector4 unknown,
    List<Md6ModelMeshWound> meshWounds
) {
    public static Md6ModelWound read(DataSource source) throws IOException {
        var name = source.readPString();
        var unknown = Vector4.read(source);
        var meshWounds = source.readObjects(source.readInt(), Md6ModelMeshWound::read);

        return new Md6ModelWound(
            name,
            unknown,
            meshWounds
        );
    }
}
