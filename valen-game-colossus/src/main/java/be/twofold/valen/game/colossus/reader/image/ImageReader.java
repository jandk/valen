package be.twofold.valen.game.colossus.reader.image;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.colossus.*;
import be.twofold.valen.game.colossus.resource.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public final class ImageReader implements AssetReader.Binary<Texture, ColossusAsset> {
    @Override
    public boolean canRead(ColossusAsset asset) {
        return asset.id().type() == ResourceType.image;
    }

    @Override
    public Texture read(BinarySource source, ColossusAsset asset, LoadingContext context) throws IOException {
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
                imageFormat,
                mip.mipPixelWidth(),
                mip.mipPixelHeight(),
                1,
                image.mipData()[i]
            ));
        }

        for (var i = 0; i < image.header().startMip(); i++) {
            var mip = image.mips().get(i);
            var mipHash = asset.hash() << 4 | (image.header().mipCount() - mip.mipLevel());

            var surface = switch (mip.compressionMode()) {
                case 1 -> readMode1Mip(context, mipHash, mip, imageFormat);
                case 2 -> readMode2Mip(context, mipHash, mip, imageFormat);
                default -> throw new IOException("Unknown compression mode: " + mip.compressionMode());
            };
            if (surface != null) {
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

        var format = surfaces.get(minMip).format();
        var kind = switch (image.header().textureType()) {
            case TT_2D -> TextureKind.TEXTURE_2D;
            case TT_3D -> TextureKind.TEXTURE_3D;
            case TT_CUBIC -> TextureKind.CUBE_MAP;
        };
        var lastMip = surfaces.size();
        for (int i = minMip; i < surfaces.size(); i++) {
            if (surfaces.get(i).format() != format) {
                lastMip = i;
                break;
            }
        }

        return new Texture(
            format,
            kind,
            image.mips().get(minMip).mipPixelWidth(),
            image.mips().get(minMip).mipPixelHeight(),
            1,
            surfaces.subList(minMip, lastMip),
            UnaryOperator.identity()
        );
    }

    private Surface readMode1Mip(LoadingContext context, long mipHash, ImageMip mip, TextureFormat format) throws IOException {
        var bytes = context.open(new ColossusStreamLocation(mipHash, mip.compressedSize(), mip.decompressedSize()));
        if (bytes.length() == 0) {
            return null;
        }
        return new Surface(format, mip.mipPixelWidth(), mip.mipPixelHeight(), 1, bytes);
    }

    private Surface readMode2Mip(LoadingContext context, long mipHash, ImageMip mip, TextureFormat textureFormat) throws IOException {
        var bytes = context.open(new ColossusStreamLocation(mipHash, mip.compressedSize(), mip.compressedSize()));
        if (bytes.length() == 0) {
            return null;
        }
        var source = BinarySource.wrap(bytes);

        var surfaceFormat = switch (textureFormat) {
            case BC4_UNORM -> TextureFormat.R8_UNORM;
            case BC5_UNORM -> TextureFormat.R8G8_UNORM;
            default -> throw new UnsupportedOperationException("Unsupported texture format: " + textureFormat);
        };

        var tileFormat = switch (textureFormat) {
            case BC4_UNORM -> 24;
            case BC5_UNORM -> 25;
            default -> throw new UnsupportedOperationException("Unsupported texture format: " + textureFormat);
        };

        var decompressor = Decompressors.getOodle();
        var surface = Surface.create(mip.mipPixelWidth(), mip.mipPixelHeight(), 1, surfaceFormat);
        while (source.position() < source.size()) {
            var tile = ImageTile.read(source);
            Check.state(tile.format() == tileFormat, "Tile format mismatch");

            var tileData = decompressor.decompress(tile.data(), tile.size()).toArray();
            byte[] decoded = WbpDecoder.decode(tile, tileData);
            var tileSurface = new Surface(surfaceFormat, tile.width(), tile.height(), 1, Bytes.wrap(decoded));
            Surface.copy(
                tileSurface, 0, 0,
                surface, tile.x(), tile.y(),
                tile.width(), tile.height()
            );
        }
        return surface;
    }

    private static TextureFormat toImageFormat(ImageTextureFormat format) {
        return switch (format) {
            case FMT_ALPHA -> TextureFormat.R8_UNORM;
            case FMT_BC1, FMT_BC1_ZERO_ALPHA -> TextureFormat.BC1_UNORM;
            case FMT_BC1_SRGB -> TextureFormat.BC1_SRGB;
            case FMT_BC3 -> TextureFormat.BC3_UNORM;
            case FMT_BC3_SRGB -> TextureFormat.BC3_SRGB;
            case FMT_BC4 -> TextureFormat.BC4_UNORM;
            case FMT_BC5 -> TextureFormat.BC5_UNORM;
            case FMT_BC6H_UF16 -> TextureFormat.BC6H_UFLOAT;
            case FMT_BC7 -> TextureFormat.BC7_UNORM;
            case FMT_BC7_SRGB -> TextureFormat.BC7_SRGB;
            case FMT_RG16F -> TextureFormat.R16G16_SFLOAT;
            case FMT_RG8 -> TextureFormat.R8G8_UNORM;
            case FMT_RGBA8 -> TextureFormat.R8G8B8A8_UNORM;
            case FMT_X16F -> TextureFormat.R16_SFLOAT;
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }
}
