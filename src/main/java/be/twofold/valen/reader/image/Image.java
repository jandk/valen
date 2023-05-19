package be.twofold.valen.reader.image;

import java.util.*;

public record Image(
    ImageHeader header,
    List<ImageMip> mips,
    byte[][] mipData
) {
}
