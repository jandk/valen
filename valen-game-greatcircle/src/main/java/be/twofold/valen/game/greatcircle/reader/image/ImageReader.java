package be.twofold.valen.game.greatcircle.reader.image;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.resource.*;
import be.twofold.valen.game.idtech.defines.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class ImageReader implements AssetReader.Binary<Texture, GreatCircleAsset> {
    private final boolean readStreams;

    public ImageReader(boolean readStreams) {
        this.readStreams = readStreams;
    }

    @Override
    public boolean canRead(GreatCircleAsset asset) {
        return asset.id().type() == ResourceType.image
            && !asset.id().name().filename().endsWith(".bimage");
    }

    @Override
    public Texture read(BinarySource source, GreatCircleAsset asset, LoadingContext context) throws IOException {
        var image = Image.read(source);
        source.expectEnd();

        if (readStreams) {
            /*
             * Not entirely sure, but it seems to work, so I'm calling it the "single stream" format
             * Specified by a boolean at offset 0x38 in the header. Could mean something else though...
             * What is also strange is that the "single stream" format is used only for light probes.
             */
            if (image.header().singleStream()) {
                throw new UnsupportedOperationException("Single stream not supported");
                // readSingleStream(image, hash);
            } else {
                readMultiStream(image, context);
            }
        }

        return map(image);
    }

    private void readSingleStream(Image image, long hash) throws IOException {
//        var lastMip = image.sliceInfos().getLast();
//        var uncompressedSize = lastMip.cumulativeSizeStreamDB() + lastMip.decompressedSize();
//        var bytes = archive.readStream(hash, uncompressedSize);
//        try (var mipSource = DataSource.fromArray(bytes)) {
//            for (var i = 0; i < image.header().totalMipCount(); i++) {
//                image.slices()[i] = mipSource.readBytes(image.sliceInfos().get(i).decompressedSize());
//            }
//        }
    }

    private void readMultiStream(Image image, LoadingContext context) throws IOException {
        for (var i = 0; i < image.header().startMip(); i++) {
            var mipInfo = image.sliceInfos().get(i);
            var mip = context.open(new GreatCircleStreamLocation(mipInfo.hash(), mipInfo.decompressedSize()));
            image.slices()[i] = mip.length() > 0 ? mip : null;
        }
    }

    private Texture map(Image image) {
        var minMip = image.minMip();
        var width = minMip < 0 ? image.header().width() : image.sliceInfos().get(minMip).width();
        var height = minMip < 0 ? image.header().height() : image.sliceInfos().get(minMip).height();
        var format = toImageFormat(image.header().textureFormat());
        var surfaces = convertMipMaps(image, format);
        var isCubeMap = image.header().type() == TextureType.TT_CUBIC;

        return new Texture(width, height, format, isCubeMap, surfaces, image.header().scale(), image.header().bias());
    }

    private List<Surface> convertMipMaps(Image image, be.twofold.valen.core.texture.TextureFormat format) {
        var faces = image.header().type() == TextureType.TT_CUBIC ? 6 : 1;
        var mipCount = image.sliceInfos().size() / faces;
        var minMip = image.minMip() < 0 ? mipCount : image.minMip();

        var surfaces = new ArrayList<Surface>();
        for (var face = 0; face < faces; face++) {
            for (var mip = minMip; mip < mipCount; mip++) {
                var mipIndex = mip * faces + face;
                if (image.slices()[mipIndex] == null) {
                    // Well, if this happens in a cube map...
                    break;
                }
                surfaces.add(new Surface(
                    image.sliceInfos().get(mipIndex).width(),
                    image.sliceInfos().get(mipIndex).height(),
                    format,
                    image.slices()[mipIndex].toArray()
                ));
            }
        }
        return List.copyOf(surfaces);
    }

    private be.twofold.valen.core.texture.TextureFormat toImageFormat(be.twofold.valen.game.idtech.defines.TextureFormat format) {
        // I might not be sure about all these mappings, but it's a start
        return switch (format) {
            case FMT_BC1, FMT_BC1_ZERO_ALPHA -> be.twofold.valen.core.texture.TextureFormat.BC1_UNORM;
            case FMT_BC1_SRGB -> be.twofold.valen.core.texture.TextureFormat.BC1_SRGB;
            case FMT_BC3 -> be.twofold.valen.core.texture.TextureFormat.BC3_UNORM;
            case FMT_BC3_SRGB -> be.twofold.valen.core.texture.TextureFormat.BC3_SRGB;
            case FMT_BC4 -> be.twofold.valen.core.texture.TextureFormat.BC4_UNORM;
            case FMT_BC5 -> be.twofold.valen.core.texture.TextureFormat.BC5_UNORM;
            case FMT_BC6H_UF16 -> be.twofold.valen.core.texture.TextureFormat.BC6H_UFLOAT;
            case FMT_BC7 -> be.twofold.valen.core.texture.TextureFormat.BC7_UNORM;
            case FMT_BC7_SRGB -> be.twofold.valen.core.texture.TextureFormat.BC7_SRGB;
            case FMT_R8 -> be.twofold.valen.core.texture.TextureFormat.R8_UNORM;
            case FMT_RG16F -> be.twofold.valen.core.texture.TextureFormat.R16G16_SFLOAT;
            case FMT_RG8 -> be.twofold.valen.core.texture.TextureFormat.R8G8_UNORM;
            case FMT_RGBA8 -> be.twofold.valen.core.texture.TextureFormat.R8G8B8A8_UNORM;
            case FMT_X16 -> be.twofold.valen.core.texture.TextureFormat.R16_UNORM;
            case FMT_X16F -> be.twofold.valen.core.texture.TextureFormat.R16_SFLOAT;
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }
}
