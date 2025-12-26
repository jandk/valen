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
    public static DeformModelHeader read(BinarySource source) throws IOException {
        var modelAsset = source.readString(StringFormat.INT_LENGTH);
        var psdRig = source.readString(StringFormat.INT_LENGTH);
        var hcRig = source.readString(StringFormat.INT_LENGTH);
        var bones1 = source.readStrings(source.readInt(), StringFormat.INT_LENGTH);
        float unknown = source.readFloat();
        var bones2 = source.readStrings(source.readInt(), StringFormat.INT_LENGTH);
        for (int i = 0; i < 5; i++) {
            source.expectInt(0);
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
