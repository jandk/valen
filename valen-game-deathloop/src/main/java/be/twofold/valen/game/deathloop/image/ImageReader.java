package be.twofold.valen.game.deathloop.image;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.deathloop.*;

import java.io.*;
import java.util.*;

public final class ImageReader implements AssetReader<Texture, DeathloopAsset> {
    private final DeathloopArchive archive;
    private final boolean readExternal = false;

    public ImageReader(DeathloopArchive archive) {
        this.archive = Objects.requireNonNull(archive);
    }

    @Override
    public boolean canRead(DeathloopAsset asset) {
        return asset.entry().typeName().equals("image");
    }

    @Override
    public Texture read(DataSource source, DeathloopAsset asset) throws IOException {
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
                var mipData = archive.loadAsset(new DeathloopAssetID(asset.id().fullName() + "_mip" + i), byte[].class);
                var surface = new Surface(w, h, mipData);
                surfaces.add(surface);
            }
        }

        int width = header.embeddedWidth();
        int height = header.embeddedHeight();
        for (int mip = 0; mip < header.embeddedLevels(); mip++) {
            for (int slice = 0; slice < depth; slice++) {
                var sliceHeader = ImageMipHeader.read(source);
                var sliceData = source.readBytes(sliceHeader.dataSize());
                var surface = new Surface(width, height, sliceData);
                surfaces.add(surface);
            }
            width /= 2;
            height /= 2;
        }
        source.expectEnd();

        return new Texture(header.width(), header.height(), format, false, surfaces, 1.0f, 0.0f);
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
