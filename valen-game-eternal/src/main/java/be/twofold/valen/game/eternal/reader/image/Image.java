package be.twofold.valen.game.eternal.reader.image;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public record Image(
    ImageHeader header,
    List<ImageMipInfo> mipInfos,
    Bytes[] mipData
) {
    public static Image read(BinarySource source) throws IOException {
        var header = ImageHeader.read(source);
        var mipInfos = source.readObjects(header.totalMipCount(), ImageMipInfo::read);
        var mipData = new Bytes[mipInfos.size()];

        for (int i = header.startMip(); i < header.totalMipCount(); i++) {
            mipData[i] = source.readBytes(mipInfos.get(i).decompressedSize());
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
