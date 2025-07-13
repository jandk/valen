package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;
import java.util.*;

public record Md6AnimStreamInfo(
    List<Md6AnimStreamDiskLayout> layouts,
    short[] streamFrameSetOffsets,
    short[] framsetToStreamLayout
) {
    private static final short[] EMPTY = new short[0];

    public static Md6AnimStreamInfo read(BinaryReader reader) throws IOException {
        var numLayouts = reader.readShort();

        List<Md6AnimStreamDiskLayout> layouts = List.of();
        short[] streamFrameSetOffsets = EMPTY;
        short[] framsetToStreamLayout = EMPTY;

        if (numLayouts > 0) {
            var numOffsets = reader.readShort();
            layouts = reader.readObjects(numLayouts, Md6AnimStreamDiskLayout::read);
            streamFrameSetOffsets = reader.readShorts(numOffsets);
            framsetToStreamLayout = reader.readShorts(numOffsets);
        }

        return new Md6AnimStreamInfo(
            layouts,
            streamFrameSetOffsets,
            framsetToStreamLayout
        );
    }
}
