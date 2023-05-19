package be.twofold.valen.reader.image;

import java.nio.*;
import java.util.*;

public record ImageHeader(
    byte version,
    ImageTextureType textureType,
    ImageTextureMaterialKind textureMaterialKind,
    int pixelWidth,
    int pixelHeight,
    int depth,
    int mipCount,
    float unkFloat1,
    float unkFloat2,
    float unkFloat3,
    ImageTextureFormat textureFormat,
    boolean streamed,
    boolean unkBool1,
    boolean unkBool2,
    boolean unkBool3,
    int streamDBMipCount
) {
    static final int Size = 0x3f;
    private static final byte[] Magic = {0x42, 0x49, 0x4d};

    public static ImageHeader read(ByteBuffer buffer) {
        byte[] magic = new byte[3];
        buffer.get(magic);
        if (!Arrays.equals(magic, Magic)) {
            throw new IllegalArgumentException("Invalid magic, expected %s, got %s".formatted(
                HexFormat.of().formatHex(Magic),
                HexFormat.of().formatHex(magic)
            ));
        }
        byte version = buffer.get(0x03);
        ImageTextureType textureType = ImageTextureType.fromCode(buffer.getInt(0x04));
        ImageTextureMaterialKind textureMaterialKind = ImageTextureMaterialKind.fromCode(buffer.getInt(0x08));
        int pixelWidth = buffer.getInt(0x0c);
        int pixelHeight = buffer.getInt(0x10);
        int depth = buffer.getInt(0x14);
        int mipCount = buffer.getInt(0x18);
        float unkFloat1 = buffer.getFloat(0x1c);
        float unkFloat2 = buffer.getFloat(0x20);
        float unkFloat3 = buffer.getFloat(0x24);
        ImageTextureFormat textureFormat = ImageTextureFormat.fromCode(buffer.getInt(0x29));
        boolean streamed = buffer.get(0x37) != 0;
        boolean unkBool1 = buffer.get(0x38) != 0;
        boolean unkBool2 = buffer.get(0x39) != 0;
        boolean unkBool3 = buffer.get(0x3a) != 0;
        int streamDBMipCount = buffer.getInt(0x3b);

        return new ImageHeader(
            version,
            textureType,
            textureMaterialKind,
            pixelWidth,
            pixelHeight,
            depth,
            mipCount,
            unkFloat1,
            unkFloat2,
            unkFloat3,
            textureFormat,
            streamed,
            unkBool1,
            unkBool2,
            unkBool3,
            streamDBMipCount
        );
    }

    public int startMip() {
        int mask = switch (textureType) {
            case TT_2D -> 0x0f;
            case TT_CUBIC -> 0xff;
            default -> throw new IllegalArgumentException("Unsupported texture type: " + textureType);
        };
        return streamDBMipCount & mask;
    }

    public int totalMipCount() {
        int faces = switch (textureType) {
            case TT_2D -> 1;
            case TT_CUBIC -> 6;
            default -> throw new IllegalArgumentException("Unsupported texture type: " + textureType);
        };
        return mipCount * faces;
    }
}
