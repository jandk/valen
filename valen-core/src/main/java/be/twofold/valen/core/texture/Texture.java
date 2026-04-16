package be.twofold.valen.core.texture;

import be.twofold.valen.core.texture.shader.*;
import be.twofold.valen.core.texture.shader.node.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;
import java.util.function.*;

public record Texture(
    TextureFormat format,
    TextureKind kind,
    int width,
    int height,
    int depthOrLayers,
    List<Surface> surfaces,
    UnaryOperator<ShaderNode> decorator
) {
    public Texture {
        Check.nonNull(format, "format");
        Check.nonNull(kind, "kind");
        Check.positive(width, "width");
        Check.positive(height, "height");
        Check.positive(depthOrLayers, "depthOrLayers");
        Check.argument(!surfaces.isEmpty(), "surfaces must not be empty");
        Check.nonNull(decorator, "decorator");

        if (kind == TextureKind.TEXTURE_1D) {
            Check.argument(height == 1, "height must be 1 for TEXTURE_1D");
        }
        if (kind == TextureKind.CUBE_MAP) {
            Check.argument(depthOrLayers == 6, "depthOrLayers must be 6 for CUBE_MAP");
        }

        if (kind == TextureKind.TEXTURE_3D) {
            for (int mip = 0; mip < surfaces.size(); mip++) {
                checkSurface(surfaces.get(mip), format,
                    Math.max(1, width >> mip),
                    Math.max(1, height >> mip),
                    Math.max(1, depthOrLayers >> mip));
            }
        } else {
            int surfaceCount = surfaces.size();
            Check.argument(surfaceCount % depthOrLayers == 0,
                () -> String.format("surfaces.size() %s is not divisible by depthOrLayers %s", surfaceCount, depthOrLayers));

            var mipCount = surfaceCount / depthOrLayers;
            for (int slice = 0; slice < depthOrLayers; slice++) {
                for (int mip = 0; mip < mipCount; mip++) {
                    checkSurface(surfaces.get(slice * mipCount + mip), format,
                        Math.max(1, width >> mip),
                        Math.max(1, height >> mip),
                        1);
                }
            }
        }

        surfaces = List.copyOf(surfaces);
    }

    private static void checkSurface(Surface surface, TextureFormat format, int width, int height, int depth) {
        Check.argument(surface.format() == format,
            () -> String.format("Surface format %s does not match texture format %s", surface.format(), format));
        Check.argument(surface.width() == width,
            () -> String.format("Surface width %s does not match expected %s", surface.width(), width));
        Check.argument(surface.height() == height,
            () -> String.format("Surface height %s does not match expected %s", surface.height(), height));
        Check.argument(surface.depth() == depth,
            () -> String.format("Surface depth %s does not match expected %s", surface.depth(), depth));
    }

    public int mipCount() {
        return kind == TextureKind.TEXTURE_3D
            ? surfaces.size()
            : surfaces.size() / depthOrLayers;
    }

    public int sliceCount(int mip) {
        return kind == TextureKind.TEXTURE_3D
            ? Math.max(1, depthOrLayers >> mip)
            : depthOrLayers;
    }

    public Surface getSurface(int mip, int slice) {
        Check.index(mip, mipCount());
        Check.index(slice, sliceCount(mip));

        if (kind == TextureKind.TEXTURE_3D) {
            var mipSurface = surfaces.get(mip);
            var sliceSize = format.surfaceSize(mipSurface.width(), mipSurface.height(), 1);
            var sliceData = mipSurface.data().slice(slice * sliceSize, sliceSize);
            return new Surface(format, mipSurface.width(), mipSurface.height(), 1, sliceData);
        }
        return surfaces.get(slice * mipCount() + mip);
    }

    public Texture convert(TextureFormat dstFormat, boolean reconstructZ) {
        var source = ShaderNode.source();
        var current = decorator.apply(source);
        if (dstFormat == format && (format.isCompressed() || current == source)) {
            return this;
        }

        if (format.channelCount() == 1 && dstFormat.channelCount() >= 3) {
            current = ShaderNode.splat(current, Channel.RED, Channel.GREEN, Channel.BLUE);
        }
        if (reconstructZ && format.channelCount() == 2 && dstFormat.channelCount() >= 3) {
            current = ShaderNode.reconstructZ(current);
        }

        var shader = Shader.of(current, dstFormat);
        var surfaces = this.surfaces.stream()
            .map(surface -> shader.execute(source.bind(surface)))
            .toList();
        return new Texture(dstFormat, kind, width, height, depthOrLayers, surfaces, UnaryOperator.identity());
    }

    public Texture convertSurface(int mip, int slice, TextureFormat dstFormat, boolean reconstructZ) {
        return withSingleSurface(getSurface(mip, slice))
            .convert(dstFormat, reconstructZ);
    }

    public Texture firstOnly() {
        return withSingleSurface(surfaces.getFirst());
    }

    // Helpers

    private Texture withSingleSurface(Surface surface) {
        return new Texture(
            format,
            TextureKind.TEXTURE_2D,
            surface.width(),
            surface.height(),
            surface.depth(),
            List.of(surface),
            decorator
        );
    }
}
