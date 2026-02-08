package be.twofold.valen.game.eternal.reader.image;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public final class ImageReader implements AssetReader<Texture, EternalAsset> {
    private final boolean readStreams;

    public ImageReader(boolean readStreams) {
        this.readStreams = readStreams;
    }

    @Override
    public boolean canRead(EternalAsset asset) {
        return asset.id().type() == ResourceType.Image;
    }

    @Override
    public Texture read(BinarySource source, EternalAsset asset, LoadingContext context) throws IOException {
        var hash = asset.hash();
        var image = Image.read(source);

        var mipData = new Bytes[image.header().totalMipCount()];
        for (var i = image.header().startMip(); i < image.header().totalMipCount(); i++) {
            mipData[i] = source.readBytes(image.mipInfos().get(i).decompressedSize());
        }
        source.expectEnd();

        if (readStreams) {
            /*
             * Not entirely sure, but it seems to work, so I'm calling it the "single stream" format
             * Specified by a boolean at offset 0x38 in the header. Could mean something else though...
             * What is also strange is that the "single stream" format is used only for light probes.
             */
            if (image.header().singleStream()) {
                readSingleStream(image, hash, mipData, context);
            } else {
                readMultiStream(image, hash, mipData, context);
            }
        }

        return map(image, mipData);
    }

    private void readSingleStream(Image image, long hash, Bytes[] mipData, LoadingContext context) throws IOException {
        var lastMip = image.mipInfos().getLast();
        var uncompressedSize = lastMip.cumulativeSizeStreamDb() + lastMip.decompressedSize();
        var bytes = context.open(new EternalStreamLocation(hash, uncompressedSize));
        try (var mipSource = BinarySource.wrap(bytes)) {
            for (var i = 0; i < image.header().totalMipCount(); i++) {
                mipData[i] = mipSource.readBytes(image.mipInfos().get(i).decompressedSize());
            }
        }
    }

    private void readMultiStream(Image image, long hash, Bytes[] mipData, LoadingContext context) throws IOException {
        for (var i = 0; i < image.header().startMip(); i++) {
            var mipInfo = image.mipInfos().get(i);
            var mipHash = hash << 4 | (image.header().mipCount() - mipInfo.mipLevel());
            var mip = context.open(new EternalStreamLocation(mipHash, mipInfo.decompressedSize()));
            mipData[i] = mip.length() > 0 ? mip : null;
        }
    }

    private Texture map(Image image, Bytes[] mipData) {
        var minMip = minMip(mipData);
        var width = minMip < 0 ? image.header().pixelWidth() : image.mipInfos().get(minMip).mipPixelWidth();
        var height = minMip < 0 ? image.header().pixelHeight() : image.mipInfos().get(minMip).mipPixelHeight();
        var format = toImageFormat(image.header().textureFormat());
        var surfaces = convertMipMaps(image, mipData, minMip, format);
        var isCubeMap = image.header().textureType() == TextureType.TT_CUBIC;
        var scale = image.header().albedoSpecularScale();
        var bias = image.header().albedoSpecularBias();

        return new Texture(width, height, format, isCubeMap, surfaces, scale, bias);
    }

    private List<Surface> convertMipMaps(Image image, Bytes[] mipData, int minMip, be.twofold.valen.core.texture.TextureFormat format) {
        var faces = image.header().textureType() == TextureType.TT_CUBIC ? 6 : 1;
        var mipCount = image.mipInfos().size() / faces;

        var surfaces = new ArrayList<Surface>();
        for (var face = 0; face < faces; face++) {
            for (var mip = minMip; mip < mipCount; mip++) {
                var mipIndex = mip * faces + face;
                surfaces.add(new Surface(
                    image.mipInfos().get(mipIndex).mipPixelWidth(),
                    image.mipInfos().get(mipIndex).mipPixelHeight(),
                    format,
                    mipData[mipIndex].toArray()
                ));
            }
        }
        return List.copyOf(surfaces);
    }

    private be.twofold.valen.core.texture.TextureFormat toImageFormat(TextureFormat format) {
        // I might not be sure about all these mappings, but it's a start
        return switch (format) {
            case FMT_ALPHA -> be.twofold.valen.core.texture.TextureFormat.R8_UNORM;
            case FMT_BC1, FMT_BC1_ZERO_ALPHA -> be.twofold.valen.core.texture.TextureFormat.BC1_UNORM;
            case FMT_BC1_SRGB -> be.twofold.valen.core.texture.TextureFormat.BC1_SRGB;
            case FMT_BC3 -> be.twofold.valen.core.texture.TextureFormat.BC3_UNORM;
            case FMT_BC3_SRGB -> be.twofold.valen.core.texture.TextureFormat.BC3_SRGB;
            case FMT_BC4 -> be.twofold.valen.core.texture.TextureFormat.BC4_UNORM;
            case FMT_BC5 -> be.twofold.valen.core.texture.TextureFormat.BC5_UNORM;
            case FMT_BC6H_UF16 -> be.twofold.valen.core.texture.TextureFormat.BC6H_UFLOAT;
            case FMT_BC7 -> be.twofold.valen.core.texture.TextureFormat.BC7_UNORM;
            case FMT_BC7_SRGB -> be.twofold.valen.core.texture.TextureFormat.BC7_SRGB;
            case FMT_RG16F -> be.twofold.valen.core.texture.TextureFormat.R16G16_SFLOAT;
            case FMT_RG8 -> be.twofold.valen.core.texture.TextureFormat.R8G8_UNORM;
            case FMT_RGBA8 -> be.twofold.valen.core.texture.TextureFormat.R8G8B8A8_UNORM;
            case FMT_X16F -> be.twofold.valen.core.texture.TextureFormat.R16_SFLOAT;
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }

    private int minMip(Bytes[] mipData) {
        return IntStream.range(0, mipData.length)
            .filter(i -> mipData[i] != null)
            .findFirst()
            .orElse(-1);
    }
}
