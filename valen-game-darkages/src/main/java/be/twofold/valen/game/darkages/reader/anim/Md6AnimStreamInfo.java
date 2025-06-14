package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record Md6AnimStreamInfo(
    List<Md6AnimStreamDiskLayout> layouts,
    short[] streamFrameSetOffsets,
    short[] framsetToStreamLayout
) {
    private static final short[] EMPTY = new short[0];

    public static Md6AnimStreamInfo read(DataSource source) throws IOException {
        var numLayouts = source.readShort();

        List<Md6AnimStreamDiskLayout> layouts = List.of();
        short[] streamFrameSetOffsets = EMPTY;
        short[] framsetToStreamLayout = EMPTY;

        if (numLayouts > 0) {
            var numOffsets = source.readShort();
            layouts = source.readObjects(numLayouts, Md6AnimStreamDiskLayout::read);
            streamFrameSetOffsets = source.readShorts(numOffsets);
            framsetToStreamLayout = source.readShorts(numOffsets);
        }

        return new Md6AnimStreamInfo(
            layouts,
            streamFrameSetOffsets,
            framsetToStreamLayout
        );
    }
}
