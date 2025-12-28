package be.twofold.valen.game.colossus.reader.image;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public record Image(
    ImageHeader header,
    List<ImageMip> mips,
    Bytes[] mipData
) {
    public static Image read(BinarySource source) throws IOException {
        var header = ImageHeader.read(source);
        var mips = source.readObjects(header.mipCount(), ImageMip::read);

        var mipData = new Bytes[mips.size()];
        for (int i = header.startMip(); i < header.totalMipCount(); i++) {
            mipData[i] = source.readBytes(mips.get(i).decompressedSize());
        }
        return new Image(header, mips, mipData);
    }

    public int minMip() {
        return IntStream.range(0, mipData.length)
            .filter(i -> mipData[i] != null)
            .findFirst()
            .orElse(-1);
    }
}
