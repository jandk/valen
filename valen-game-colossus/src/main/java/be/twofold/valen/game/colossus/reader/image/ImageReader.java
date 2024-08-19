package be.twofold.valen.game.colossus.reader.image;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.colossus.*;
import be.twofold.valen.game.colossus.reader.*;
import be.twofold.valen.game.colossus.resource.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class ImageReader implements ResourceReader<Texture> {
    private final ColossusArchive archive;
    private final boolean readStreams;

    public ImageReader(ColossusArchive archive) {
        this(archive, true);
    }

    ImageReader(ColossusArchive archive, boolean readStreams) {
        this.archive = archive;
        this.readStreams = readStreams;
    }

    @Override
    public boolean canRead(ResourceKey key) {
        return key.type() == ResourceType.image;
    }

    @Override
    public Texture read(DataSource source, Asset asset) throws IOException {
        var image = Image.read(source);
        source.expectEnd();

        var imageFormat = toImageFormat(image.header().textureFormat());

        var surfaces = new ArrayList<Surface>();
        for (var i = 0; i < image.header().mipCount(); i++) {
            if (image.mipData()[i] == null) {
                surfaces.add(null);
                continue;
            }

            var mip = image.mips().get(i);
            surfaces.add(new Surface(
                mip.mipPixelWidth(),
                mip.mipPixelHeight(),
                imageFormat,
                image.mipData()[i]
            ));
        }

        long hash = (Long) asset.properties().get("hash");
        for (var i = 0; i < image.header().startMip(); i++) {
            var mip = image.mips().get(i);
            var mipHash = hash << 4 | (image.header().mipCount() - mip.mipLevel());

            if (archive.containsStream(mipHash)) {
                var surface = switch (mip.compressionMode()) {
                    case 1 -> readMode1Mip(mipHash, mip, imageFormat);
                    case 2 -> readMode2Mip(mipHash, mip, imageFormat);
                    default -> throw new IOException("Unknown compression mode: " + mip.compressionMode());
                };
                surfaces.set(i, surface);
            }
        }

        int minMip = surfaces.size();
        for (int i = 0; i < surfaces.size(); i++) {
            if (surfaces.get(i) != null) {
                minMip = i;
                break;
            }
        }

        Surface surface = surfaces.get(minMip);
        // saveImage(surface.data(), surface.width(), surface.height(), "D:\\Jan\\Desktop\\colossus\\test.png");

        return new Texture(
            image.mips().get(minMip).mipPixelWidth(),
            image.mips().get(minMip).mipPixelHeight(),
            imageFormat,
            surfaces.subList(minMip, image.header().mipCount()),
            image.header().textureType() == ImageTextureType.TT_CUBIC
        );
    }

    private Surface readMode1Mip(long mipHash, ImageMip mip, TextureFormat format) throws IOException {
        var bytes = archive.readStream(mipHash, mip.compressedSize(), mip.decompressedSize());
        return new Surface(mip.mipPixelWidth(), mip.mipPixelHeight(), format, bytes);
    }

    private Surface readMode2Mip(long mipHash, ImageMip mip, TextureFormat textureFormat) throws IOException {
        var bytes = archive.readStreamRaw(mipHash, mip.compressedSize());
        var source = new ByteArrayDataSource(bytes);

        var surfaceFormat = switch (textureFormat) {
            case Bc4UNorm -> TextureFormat.R8UNorm;
            case Bc5UNorm -> TextureFormat.R8G8UNorm;
            default -> throw new UnsupportedOperationException("Unsupported texture format: " + textureFormat);
        };

        var tileFormat = switch (textureFormat) {
            case Bc4UNorm -> 24;
            case Bc5UNorm -> 25;
            default -> throw new UnsupportedOperationException("Unsupported texture format: " + textureFormat);
        };

        var surface = Surface.create(mip.mipPixelWidth(), mip.mipPixelHeight(), surfaceFormat);
        while (source.tell() < source.size()) {
            var tile = ImageTile.read(source);
            Check.state(tile.format() == tileFormat, "Tile format mismatch");

            var tileData = Buffers.toArray(Decompressor
                .forType(CompressionType.Kraken)
                .decompress(ByteBuffer.wrap(tile.data()), tile.size()));

            byte[] decoded = WbpDecoder.decode(tile, tileData);
            var tileSurface = new Surface(tile.width(), tile.height(), surfaceFormat, decoded);
            surface.copyFrom(tileSurface, tile.x(), tile.y());
            System.out.println(tile);
        }
        return surface;
    }

    private static TextureFormat toImageFormat(ImageTextureFormat format) {
        // I might not be sure about all these mappings, but it's a start
        return switch (format) {
            case FMT_ALPHA -> TextureFormat.R8UNorm;
            case FMT_BC1, FMT_BC1_ZERO_ALPHA -> TextureFormat.Bc1UNorm;
            case FMT_BC1_SRGB -> TextureFormat.Bc1UNormSrgb;
            case FMT_BC3 -> TextureFormat.Bc3UNorm;
            case FMT_BC3_SRGB -> TextureFormat.Bc3UNormSrgb;
            case FMT_BC4 -> TextureFormat.Bc4UNorm;
            case FMT_BC5 -> TextureFormat.Bc5UNorm;
            case FMT_BC6H_UF16 -> TextureFormat.Bc6HUFloat16;
            case FMT_BC7 -> TextureFormat.Bc7UNorm;
            case FMT_BC7_SRGB -> TextureFormat.Bc7UNormSrgb;
            case FMT_RG16F -> TextureFormat.R16G16Float;
            case FMT_RG8 -> TextureFormat.R8G8UNorm;
            case FMT_RGBA8 -> TextureFormat.R8G8B8A8UNorm;
            case FMT_X16F -> TextureFormat.R16Float;
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }
}
