package be.twofold.valen.game.doom.readers.image;

import be.twofold.valen.game.idtech.reader.image.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.time.*;

public record ImageHeader(
    Instant timestamp,
    TextureType type,
    int width,
    int height,
    int depth,
    int mipCount,
    boolean environment,
    TextureFormat format,
    int filter,
    int repeat
) {
    public static ImageHeader read(BinaryReader reader) throws IOException {
        var timestamp = Instant.ofEpochSecond(reader.readInt());
        reader.expectInt(0x074D_4942);
        var type = TextureType.read(reader);
        var width = reader.readInt();
        var height = reader.readInt();
        var depth = reader.readInt();
        var mipCount = reader.readInt();
        var environment = reader.readBool(BoolFormat.BYTE);
        var format = toTextureFormat(reader.readInt());
        var filter = reader.readInt();
        var repeat = reader.readInt();
        reader.expectZeros(1);

        return new ImageHeader(
            timestamp,
            type,
            width,
            height,
            depth,
            mipCount,
            environment,
            format,
            filter,
            repeat
        );
    }

    private static TextureFormat toTextureFormat(int value) {
        return switch (value) {
            case 2 -> TextureFormat.FMT_RGBA16F;
            case 3 -> TextureFormat.FMT_RGBA8;
            case 5 -> TextureFormat.FMT_ALPHA;
            case 6 -> TextureFormat.FMT_L8A8;
            case 10 -> TextureFormat.FMT_BC1;
            case 11 -> TextureFormat.FMT_BC3;
            case 22 -> TextureFormat.FMT_BC6H_UF16;
            case 23 -> TextureFormat.FMT_BC7;
            case 24 -> TextureFormat.FMT_BC4;
            case 25 -> TextureFormat.FMT_BC5;
            default -> throw new UnsupportedOperationException("Unsupported texture format value " + value);
        };
    }

    int totalMipCount() {
        var faces = switch (type) {
            case TT_2D, TT_3D -> 1;
            case TT_CUBIC -> 6;
        };
        return mipCount * faces;
    }
}
