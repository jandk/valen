package be.twofold.valen.game.eternal.reader.image;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Image(
    ImageHeader header,
    List<ImageMipInfo> mipInfos
) {
    public static Image read(BinarySource source) throws IOException {
        var header = ImageHeader.read(source);
        var mipInfos = source.readObjects(header.mipCount(), ImageMipInfo::read);

        return new Image(
            header,
            mipInfos
        );
    }
}
