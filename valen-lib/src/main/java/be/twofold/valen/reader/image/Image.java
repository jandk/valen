package be.twofold.valen.reader.image;

import be.twofold.valen.core.util.*;

import java.util.*;
import java.util.stream.*;

public record Image(
    ImageHeader header,
    List<ImageMipInfo> mipInfos,
    byte[][] mipData
) {
    public static Image read(BetterBuffer buffer) {
        var header = ImageHeader.read(buffer);
        var mipInfos = buffer.getStructs(header.totalMipCount(), ImageMipInfo::read);
        var mipData = new byte[mipInfos.size()][];

        for (int i = header.startMip(); i < header.totalMipCount(); i++) {
            mipData[i] = buffer.getBytes(mipInfos.get(i).decompressedSize());
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
