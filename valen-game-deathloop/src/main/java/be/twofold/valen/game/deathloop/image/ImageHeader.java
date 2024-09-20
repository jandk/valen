package be.twofold.valen.game.deathloop.image;

import be.twofold.valen.core.io.*;

import java.io.*;

public record ImageHeader(
    ImageTextureType textureType,
    ImageTextureFormat textureFormat,
    int unk2,
    int unk3,
    int embeddedWidth,
    int embeddedHeight,
    int width,
    int height,
    int depth,
    int embeddedLevels,
    int levels,
    int unk4,
    int unk5,
    int unk6
) {
    public static ImageHeader read(DataSource source) throws IOException {
        source.expectInt(20);
        var textureType = ImageTextureType.fromCode(source.readInt());
        var textureFormat = ImageTextureFormat.fromCode(source.readInt());
        var unk2 = source.readInt();
        var unk3 = source.readInt();
        var embeddedWidth = source.readInt();
        var embeddedHeight = source.readInt();
        var width = source.readInt();
        var height = source.readInt();
        var depth = source.readInt();
        var embeddedLevels = source.readInt();
        var levels = source.readInt();
        source.expectInt(0);
        var unk4 = source.readInt();
        source.expectByte((byte) 0);
        source.expectByte((byte) 0);
        source.expectByte((byte) 0);
        var unk5 = source.readInt();
        var unk6 = source.readInt();

        return new ImageHeader(
            textureType,
            textureFormat,
            unk2,
            unk3,
            embeddedWidth,
            embeddedHeight,
            width,
            height,
            depth,
            embeddedLevels,
            levels,
            unk4,
            unk5,
            unk6
        );
    }
}
