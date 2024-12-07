package be.twofold.valen.game.greatcircle.reader.image;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public record Image(
    ImageHeader header,
    List<ImageSlice> sliceInfos,
    byte[][] slices
) {
    public static Image read(DataSource source) throws IOException {
        var header = ImageHeader.read(source);

        var numSlices = header.mipCount() * Math.max(header.count(), 1);
        if (header.type() == ImageTextureType.TT_CUBIC) {
            numSlices *= 6;
        }

        var slices = new byte[numSlices][];
        var sliceInfos = source.readStructs(numSlices, ImageSlice::read);
        for (int i = header.startMip(); i < sliceInfos.size(); i++) {
            slices[i] = source.readBytes(sliceInfos.get(i).size());
        }

        source.expectEnd();

        return new Image(
            header,
            sliceInfos,
            slices
        );
    }

    public int minMip() {
        return IntStream.range(0, slices.length)
            .filter(i -> slices[i] != null)
            .findFirst()
            .orElse(-1);
    }
}
