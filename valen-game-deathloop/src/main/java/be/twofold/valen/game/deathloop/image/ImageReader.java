package be.twofold.valen.game.deathloop.image;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.deathloop.*;
import be.twofold.valen.game.deathloop.index.*;

import java.io.*;
import java.util.*;

public final class ImageReader {
    private final DeathloopArchive archive;
    private final boolean readExternal = false;

    public ImageReader(DeathloopArchive archive) {
        this.archive = Objects.requireNonNull(archive);
    }

    public Texture read(DataSource source, IndexEntry entry) throws IOException {
        var header = ImageHeader.read(source);
        var format = mapFormat(header.textureFormat());
        int depth = header.textureType() == ImageTextureType.TT_3D ? header.depth() : header.numSlices();
        if (header.textureType() == ImageTextureType.TT_CUBIC && depth == 1) {
            depth = 6;
        }

        var surfaces = new ArrayList<Surface>();
        if (readExternal) {
            var externalLevels = header.levels() - header.embeddedLevels();
            for (int i = 0, w = header.width(), h = header.height(); i < externalLevels; i++, w /= 2, h /= 2) {
                var mipData = archive.loadRawAsset(new DeathloopAssetID(entry.fileName() + "_mip" + i));
                var surface = new Surface(w, h, format, Buffers.toArray(mipData));
                surfaces.add(surface);
            }
        }

        int width = header.embeddedWidth();
        int height = header.embeddedHeight();
        for (int mip = 0; mip < header.embeddedLevels(); mip++) {
            for (int slice = 0; slice < depth; slice++) {
                var sliceHeader = ImageMipHeader.read(source);
                var sliceData = source.readBytes(sliceHeader.dataSize());
                var surface = new Surface(width, height, format, sliceData);
                surfaces.add(surface);
            }
            width /= 2;
            height /= 2;
        }
        source.expectEnd();

        return new Texture(header.width(), header.height(), format, surfaces, false);
    }

    private TextureFormat mapFormat(ImageTextureFormat textureFormat) {
        return switch (textureFormat) {
            case FMT_R8 -> TextureFormat.R8_UNORM;
            case FMT_R16F -> TextureFormat.R16_SFLOAT;
            case FMT_R32F -> TextureFormat.R32_SFLOAT;
            case FMT_R16_UNORM -> TextureFormat.R16_UNORM;
            case FMT_RG32F -> TextureFormat.R32G32_SFLOAT;
            case FMT_RGBA -> TextureFormat.R8G8B8A8_UNORM;
            case FMT_RGBA16F -> TextureFormat.R16G16B16A16_SFLOAT;
            case FMT_RGBA32F -> TextureFormat.R32G32B32A32_SFLOAT;
            case FMT_BC1 -> TextureFormat.BC1_SRGB;
            case FMT_BC3 -> TextureFormat.BC3_SRGB;
            case FMT_BC4 -> TextureFormat.BC4_UNORM;
            case FMT_BC5S -> TextureFormat.BC5_SNORM;
            case FMT_BC6H -> TextureFormat.BC6H_UFLOAT;
            case FMT_BC7 -> TextureFormat.BC7_SRGB;
        };
    }
}
