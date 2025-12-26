package be.twofold.valen.game.greatcircle.reader.image;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.idtech.defines.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public record Image(
    ImageHeader header,
    List<ImageSlice> sliceInfos,
    Bytes[] slices
) {
    public static Image read(BinarySource source) throws IOException {
        var header = ImageHeader.read(source);

        var numSlices = header.mipCount() * Math.max(header.count(), 1);
        if (header.type() == TextureType.TT_CUBIC) {
            numSlices *= 6;
        }

        var slices = new Bytes[numSlices];
        var sliceInfos = source.readObjects(numSlices, ImageSlice::read);
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
