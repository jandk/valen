package be.twofold.valen.game.doom.readers.image;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Image(
    ImageHeader header,
    List<ImageMip> mips
) {
    public static Image read(BinaryReader reader) throws IOException {
        var header = ImageHeader.read(reader);

        int mipCount = header.mipCount();
        if (header.type() == 2) { // TT_CUBIC
            mipCount *= 6;
        }

        var mips = reader.readObjects(mipCount, ImageMip::read);

        return new Image(header, mips);
    }
}
