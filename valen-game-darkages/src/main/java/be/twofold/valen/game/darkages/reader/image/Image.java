package be.twofold.valen.game.darkages.reader.image;

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
    public static Image read(DataSource source) throws IOException {
        var header = ImageHeader.read(source);
        var mipInfos = source.readObjects(header.totalMipCount(), ImageMipInfo::read);
        var mipData = new ByteBuffer[mipInfos.size()];

        for (int i = header.startMip(); i < header.totalMipCount(); i++) {
            mipData[i] = source.readBuffer(mipInfos.get(i).decompressedSize());
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
