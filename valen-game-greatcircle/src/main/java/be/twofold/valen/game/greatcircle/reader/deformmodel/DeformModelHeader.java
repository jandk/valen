package be.twofold.valen.game.greatcircle.reader.deformmodel;

import be.twofold.valen.core.io.*;

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
        var modelAsset = reader.readPString();
        var psdRig = reader.readPString();
        var hcRig = reader.readPString();
        var bones1 = reader.readObjects(reader.readInt(), BinaryReader::readPString);
        float unknown = reader.readFloat();
        var bones2 = reader.readObjects(reader.readInt(), BinaryReader::readPString);
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
