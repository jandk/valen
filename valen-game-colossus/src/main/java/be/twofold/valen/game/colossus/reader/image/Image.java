package be.twofold.valen.game.colossus.reader.image;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public record Image(
    ImageHeader header,
    List<ImageMip> mips,
    byte[][] mipData
) {
    public static Image read(DataSource source) throws IOException {
        var header = ImageHeader.read(source);
        var mips = source.readStructs(header.mipCount(), ImageMip::read);

        var mipData = new byte[mips.size()][];
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
