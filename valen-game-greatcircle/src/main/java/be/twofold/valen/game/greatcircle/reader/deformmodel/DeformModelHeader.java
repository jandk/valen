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
    public static DeformModelHeader read(DataSource source) throws IOException {
        var modelAsset = source.readPString();
        var psdRig = source.readPString();
        var hcRig = source.readPString();
        var bones1 = source.readObjects(source.readInt(), DataSource::readPString);
        float unknown = source.readFloat();
        var bones2 = source.readObjects(source.readInt(), DataSource::readPString);
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
