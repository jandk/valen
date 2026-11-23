package be.twofold.valen.game.eternal.reader.image;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Image(
    ImageHeader header,
    List<ImageMipInfo> mipInfos
) {
    public static Image read(BinaryReader reader) throws IOException {
        var header = ImageHeader.read(reader);
        var mipInfos = reader.readObjects(header.totalMipCount(), ImageMipInfo::read);

        return new Image(
            header,
            mipInfos
        );
    }
}
