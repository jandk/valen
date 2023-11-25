package be.twofold.valen.reader.image;

import be.twofold.valen.*;

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

    public static ImageHeader read(BetterBuffer buffer) {
        buffer.expectByte(0x42);
        buffer.expectByte(0x49);
        buffer.expectByte(0x4d);
        byte version = buffer.getByte();

        ImageTextureType textureType = ImageTextureType.fromCode(buffer.getInt());
        ImageTextureMaterialKind textureMaterialKind = ImageTextureMaterialKind.fromCode(buffer.getInt());
        int pixelWidth = buffer.getInt();
        int pixelHeight = buffer.getInt();
        int depth = buffer.getInt();
        int mipCount = buffer.getInt();
        float unkFloat1 = buffer.getFloat();
        float unkFloat2 = buffer.getFloat();
        float unkFloat3 = buffer.getFloat();
        buffer.expectByte(0);
        ImageTextureFormat textureFormat = ImageTextureFormat.fromCode(buffer.getInt());
        buffer.expectInt(7); // always 7?
        buffer.expectInt(0); // padding
        buffer.expectShort(0); // padding
        boolean streamed = buffer.getByteAsBool();
        boolean unkBool1 = buffer.getByteAsBool();
        boolean unkBool2 = buffer.getByteAsBool();
        boolean unkBool3 = buffer.getByteAsBool();
        int streamDBMipCount = buffer.getInt();

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
