package be.twofold.valen.game.doom.readers.image;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public record Image(
    ImageHeader header,
    List<ImageMip> mips
) {
    public static Image read(BinaryReader reader) throws IOException {
        reader.order(ByteOrder.BIG_ENDIAN);
        var header = ImageHeader.read(reader);
        var mips = reader.readObjects(header.totalMipCount(), ImageMip::read);

        return new Image(
            header,
            mips
        );
    }
}
