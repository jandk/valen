package be.twofold.valen.game.darkages.reader.anim;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Md6AnimStreamInfo(
    List<Md6AnimStreamDiskLayout> layouts,
    Shorts streamFrameSetOffsets,
    Shorts framsetToStreamLayout
) {

    public static Md6AnimStreamInfo read(BinaryReader reader) throws IOException {
        var numLayouts = reader.readShort();

        List<Md6AnimStreamDiskLayout> layouts = List.of();
        Shorts streamFrameSetOffsets = Shorts.empty();
        Shorts framsetToStreamLayout = Shorts.empty();

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
