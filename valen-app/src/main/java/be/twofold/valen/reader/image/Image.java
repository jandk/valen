package be.twofold.valen.reader.image;

import java.util.*;
import java.util.stream.*;

public record Image(
    ImageHeader header,
    List<ImageMipInfos> mipInfos,
    byte[][] mipData
) {
    public int minMip() {
        return IntStream.range(0, mipData.length)
            .filter(i -> mipData[i] != null)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No mipmaps found"));
    }
}
