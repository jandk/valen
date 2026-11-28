package be.twofold.valen.game.darkages.reader.image;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.texture.shader.node.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.*;
import be.twofold.valen.game.idtech.game.*;
import be.twofold.valen.game.idtech.reader.image.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.lang.invoke.*;
import java.util.*;

public final class ImageReader implements AssetReader.Binary<Texture, DarkAgesAsset> {
    private final boolean readStreams;

    public ImageReader(boolean readStreams) {
        this.readStreams = readStreams;
    }

    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourceType.of("image");
    }

    @Override
    public Texture read(BinaryReader reader, DarkAgesAsset asset, LoadingContext context) throws IOException {
        var image = read(reader, asset.hash(), context);
        return map(image);
    }

    @Override
    public Optional<Meta.Node> readMetadata(DarkAgesAsset asset, LoadingContext context) throws IOException {
        try (var reader = BinaryReader.wrap(context.open(asset.location()))) {
            var image = Image.read(reader);
            return Optional.of(Meta.build(MethodHandles.lookup(), image));
        }
    }

    public Image read(BinaryReader reader, long hash, LoadingContext context) throws IOException {
        var image = Image.read(reader);
        reader.expectEnd();

        if (readStreams) {
            /*
             * Not entirely sure, but it seems to work, so I'm calling it the "single stream" format
             * Specified by a boolean at offset 0x38 in the header. Could mean something else though...
             * What is also strange is that the "single stream" format is used only for light probes.
             */
            if (image.header().singleStream()) {
                readSingleStream(image, hash, context);
            } else {
                readMultiStream(image, hash, context);
            }
        }

        return image;
    }

    private void readSingleStream(Image image, long hash, LoadingContext context) throws IOException {
        var lastMip = image.mipInfos().getLast();
        var uncompressedSize = lastMip.cumulativeSizeStreamDB() + lastMip.decompressedSize();
        var streamId = Hash.hash(hash, 0, 0);
        var bytes = context.open(new DarkAgesStreamLocation(streamId, uncompressedSize));
        try (var mipSource = BinaryReader.wrap(bytes)) {
            for (var i = 0; i < image.header().totalMipCount(); i++) {
                image.mipData()[i] = mipSource.readBytes(image.mipInfos().get(i).decompressedSize());
            }
        }
    }

    private void readMultiStream(Image image, long hash, LoadingContext context) throws IOException {
        for (var i = 0; i < image.header().startMip(); i++) {
            var mipInfo = image.mipInfos().get(i);
            var mipStreamId = image.header().streamDBMipCount() - mipInfo.mipLevel() - 1;
            var mipHash = Hash.hash(hash, mipStreamId, 0);
            var mip = context.open(new DarkAgesStreamLocation(mipHash, mipInfo.decompressedSize()));
            image.mipData()[i] = mip.length() > 0 ? mip : null;
        }
    }

    private Texture map(Image image) {
        var minMip = image.minMip();
        var kind = switch (image.header().textureType()) {
            case TT_2D -> TextureKind.TEXTURE_2D;
            case TT_3D -> TextureKind.TEXTURE_3D;
            case TT_CUBIC -> TextureKind.CUBE_MAP;
        };
        var format = image.header().textureFormat().toImageFormat();
        var width = minMip < 0 ? image.header().pixelWidth() : image.mipInfos().get(minMip).mipPixelWidth();
        var height = minMip < 0 ? image.header().pixelHeight() : image.mipInfos().get(minMip).mipPixelHeight();
        var depth = minMip < 0 ? image.header().depth() : image.mipInfos().get(minMip).mipPixelDepth();
        var depthOrLayers = switch (image.header().textureType()) {
            case TT_2D -> 1;
            case TT_3D -> depth;
            case TT_CUBIC -> 6;
        };
        var surfaces = convertMipMaps(image, format);

        var scale = image.header().albedoSpecularScale();
        var bias = image.header().albedoSpecularBias();
        var scaleAndBias = ShaderNode.scaleAndBias(scale, bias);

        return new Texture(format, kind, width, height, depthOrLayers, surfaces, scaleAndBias);
    }

    private List<Surface> convertMipMaps(Image image, be.twofold.valen.core.texture.TextureFormat format) {
        var faces = image.header().textureType() == TextureType.TT_CUBIC ? 6 : 1;
        var mipCount = image.mipInfos().size() / faces;
        var minMip = image.minMip() < 0 ? mipCount : image.minMip();

        var surfaces = new ArrayList<Surface>();
        for (var face = 0; face < faces; face++) {
            for (var mip = minMip; mip < mipCount; mip++) {
                var mipIndex = mip * faces + face;
                surfaces.add(new Surface(
                    format,
                    image.mipInfos().get(mipIndex).mipPixelWidth(),
                    image.mipInfos().get(mipIndex).mipPixelHeight(),
                    image.mipInfos().get(mipIndex).mipPixelDepth(),
                    image.mipData()[mipIndex]
                ));
            }
        }
        return List.copyOf(surfaces);
    }
}
