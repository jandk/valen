package be.twofold.valen.core.texture;

public enum TextureFormat {
    R8G8B8A8UNorm(1, 1, 4),
    R16G16Float(1, 1, 4),
    R8G8UNorm(1, 1, 2),
    R16Float(1, 1, 2),
    A8UNorm(1, 1, 1),
    Bc1UNorm(4, 4, 8),
    Bc1UNormSrgb(4, 4, 8),
    Bc2UNorm(4, 4, 16),
    Bc2UNormSrgb(4, 4, 16),
    Bc3UNorm(4, 4, 16),
    Bc3UNormSrgb(4, 4, 16),
    Bc4UNorm(4, 4, 8),
    Bc4SNorm(4, 4, 8),
    Bc5UNorm(4, 4, 16),
    Bc5SNorm(4, 4, 16),
    Bc6HUFloat16(4, 4, 16),
    Bc6HSFloat16(4, 4, 16),
    Bc7UNorm(4, 4, 16),
    Bc7UNormSrgb(4, 4, 16);

    private final int tileWidth;
    private final int tileHeight;
    private final int tileSizeInBytes;

    TextureFormat(int tileWidth, int tileHeight, int tileSizeInBytes) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tileSizeInBytes = tileSizeInBytes;
    }

    public int tileWidth() {
        return tileWidth;
    }

    public int tileHeight() {
        return tileHeight;
    }

    public int tileSizeInBytes() {
        return tileSizeInBytes;
    }

    public int surfaceSize(int width, int height) {
        return (width / tileWidth) * (height / tileHeight) * tileSizeInBytes;
    }
}
