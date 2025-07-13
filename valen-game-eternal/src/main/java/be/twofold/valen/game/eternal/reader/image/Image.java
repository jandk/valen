package be.twofold.valen.game.eternal.reader.image;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.stream.*;

public record Image(
    ImageHeader header,
    List<ImageMipInfo> mipInfos,
    ByteBuffer[] mipData
) {
    public static Image read(BinaryReader reader) throws IOException {
        var header = ImageHeader.read(reader);
        var mipInfos = reader.readObjects(header.totalMipCount(), ImageMipInfo::read);
        var mipData = new ByteBuffer[mipInfos.size()];

        for (int i = header.startMip(); i < header.totalMipCount(); i++) {
            mipData[i] = reader.readBuffer(mipInfos.get(i).decompressedSize());
        }
        return new Image(header, mipInfos, mipData);
    }

    public int minMip() {
        return IntStream.range(0, mipData.length)
            .filter(i -> mipData[i] != null)
            .findFirst()
            .orElse(-1);
    }
}
