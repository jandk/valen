package be.twofold.valen.reader.image;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public record Image(
    ImageHeader header,
    List<ImageMipInfo> mipInfos,
    byte[][] mipData
) {
    public static Image read(DataSource source) throws IOException {
        var header = ImageHeader.read(source);
        var mipInfos = source.readStructs(header.totalMipCount(), ImageMipInfo::read);
        var mipData = new byte[mipInfos.size()][];

        for (int i = header.startMip(); i < header.totalMipCount(); i++) {
            mipData[i] = source.readBytes(mipInfos.get(i).decompressedSize());
        }
        return new Image(header, mipInfos, mipData);
    }

    public int minMip() {
        return IntStream.range(0, mipData.length)
            .filter(i -> mipData[i] != null)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No mipmaps found"));
    }
}
