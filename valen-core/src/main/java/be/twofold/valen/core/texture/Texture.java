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
    int mipLevels,
    List<Surface> surfaces,
    UnaryOperator<ShaderNode> decorator
) {
    public Texture {
        Check.nonNull(format, "format");
        Check.nonNull(kind, "kind");
        Check.argument(width > 0, "width must be greater than 0");
        Check.argument(height > 0, "height must be greater than 0");
        Check.argument(depthOrLayers > 0, "depthOrLayers must be greater than 0");
        Check.argument(mipLevels > 0, "mipLevels must be greater than 0");

        switch (kind) {
            case TEXTURE_1D, TEXTURE_1D_ARRAY -> Check.argument(height == 1, "height must be 1 for " + kind);
        }
        switch (kind) {
            case TEXTURE_1D, TEXTURE_2D -> Check.argument(depthOrLayers == 1, "depthOrLayers must be 1 for " + kind);
            case TEXTURE_1D_ARRAY, TEXTURE_2D_ARRAY ->
                Check.argument(depthOrLayers > 1, "depthOrLayers must be greater than 1 for " + kind);
            case CUBE_MAP -> Check.argument(depthOrLayers == 6, "depthOrLayers must be 6 for CUBE_MAP");
            case TEXTURE_3D -> {
            }
        }

        var expectedSurfaces = kind == TextureKind.TEXTURE_3D ? mipLevels : depthOrLayers * mipLevels;
        Check.argument(!surfaces.isEmpty(), "surfaces must not be empty");
        Check.argument(surfaces.size() == expectedSurfaces,
            "Expected " + expectedSurfaces + " surfaces, got " + surfaces.size());

        surfaces = List.copyOf(surfaces);
    }

    public int sliceCount(int mip) {
        return kind == TextureKind.TEXTURE_3D
            ? Math.max(1, depthOrLayers >> mip)
            : depthOrLayers;
    }

    public Surface getSurface(int slice, int mip) {
        Check.argument(mip >= 0 && mip < mipLevels, "mip out of range: " + mip);
        Check.argument(slice >= 0 && slice < sliceCount(mip), "slice out of range: " + slice);

        if (kind == TextureKind.TEXTURE_3D) {
            var mipSurface = surfaces.get(mip);
            var sliceSize = format.surfaceSize(mipSurface.width(), mipSurface.height());
            var sliceData = mipSurface.data().slice(slice * sliceSize, sliceSize);
            return new Surface(mipSurface.width(), mipSurface.height(), 1, format, sliceData);
        }
        return surfaces.get(slice * mipLevels + mip);
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
        return new Texture(dstFormat, kind, width, height, depthOrLayers, mipLevels, surfaces, UnaryOperator.identity());
    }

    public Texture convertSurface(int slice, int mip, TextureFormat dstFormat, boolean reconstructZ) {
        return withSingleSurface(getSurface(slice, mip)).convert(dstFormat, reconstructZ);
    }

    public Texture firstOnly() {
        return withSingleSurface(surfaces.getFirst());
    }

    private Texture withSingleSurface(Surface surface) {
        return new Texture(
            format,
            TextureKind.TEXTURE_2D,
            surface.width(),
            surface.height(),
            surface.depth(),
            1,
            List.of(surface),
            decorator
        );
    }
}
