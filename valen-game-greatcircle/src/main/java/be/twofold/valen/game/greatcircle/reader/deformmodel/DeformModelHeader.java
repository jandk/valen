package be.twofold.valen.game.greatcircle.reader.deformmodel;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record DeformModelHeader(
    String modelAsset,
    String psdRig,
    String hcRig,
    List<String> bones1,
    float unknown,
    List<String> bones2
) {
    public static DeformModelHeader read(BinaryReader reader) throws IOException {
        var modelAsset = reader.readString(StringFormat.INT_LENGTH);
        var psdRig = reader.readString(StringFormat.INT_LENGTH);
        var hcRig = reader.readString(StringFormat.INT_LENGTH);
        var bones1 = reader.readStrings(reader.readInt(), StringFormat.INT_LENGTH);
        float unknown = reader.readFloat();
        var bones2 = reader.readStrings(reader.readInt(), StringFormat.INT_LENGTH);
        for (int i = 0; i < 5; i++) {
            reader.expectInt(0);
        }

        return new DeformModelHeader(
            modelAsset,
            psdRig,
            hcRig,
            bones1,
            unknown,
            bones2
        );
    }
}
