package be.twofold.valen.core.texture;

import be.twofold.valen.core.texture.conversion.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public record Texture(
    TextureKind kind,
    int width,
    int height,
    int depthOrLayers,
    int mipLevels,
    TextureFormat format,
    List<Surface> surfaces,
    float scale,
    float bias
) {
    public Texture {
        Check.nonNull(kind, "kind");
        Check.argument(width > 0, "width must be greater than 0");
        Check.argument(height > 0, "height must be greater than 0");
        Check.argument(depthOrLayers > 0, "depthOrLayers must be greater than 0");
        Check.argument(mipLevels > 0, "mipLevels must be greater than 0");
        Check.nonNull(format, "format");

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

    public Texture withFormat(TextureFormat format) {
        return new Texture(kind, width, height, depthOrLayers, mipLevels, format, surfaces, scale, bias);
    }

    public Texture withSurfaces(List<Surface> surfaces) {
        return new Texture(kind, width, height, depthOrLayers, mipLevels, format, surfaces, scale, bias);
    }

    public Texture withScaleAndBias(float scale, float bias) {
        return new Texture(kind, width, height, depthOrLayers, mipLevels, format, surfaces, scale, bias);
    }

    public Texture firstOnly() {
        return fromSurface(surfaces.getFirst(), format, scale, bias);
    }

    public Texture convert(TextureFormat format, boolean reconstructZ) {
        return Conversion.convert(this, format, reconstructZ);
    }

    public static Texture of1D(int width, int mipLevels, TextureFormat format, List<Surface> surfaces) {
        return new Texture(TextureKind.TEXTURE_1D, width, 1, 1, mipLevels, format, surfaces, 1.0f, 0.0f);
    }

    public static Texture of1DArray(int width, int layers, int mipLevels, TextureFormat format, List<Surface> surfaces) {
        return new Texture(TextureKind.TEXTURE_1D_ARRAY, width, 1, layers, mipLevels, format, surfaces, 1.0f, 0.0f);
    }

    public static Texture of2D(int width, int height, int mipLevels, TextureFormat format, List<Surface> surfaces) {
        return new Texture(TextureKind.TEXTURE_2D, width, height, 1, mipLevels, format, surfaces, 1.0f, 0.0f);
    }

    public static Texture of2DArray(int width, int height, int layers, int mipLevels, TextureFormat format, List<Surface> surfaces) {
        return new Texture(TextureKind.TEXTURE_2D_ARRAY, width, height, layers, mipLevels, format, surfaces, 1.0f, 0.0f);
    }

    public static Texture of3D(int width, int height, int depth, int mipLevels, TextureFormat format, List<Surface> surfaces) {
        return new Texture(TextureKind.TEXTURE_3D, width, height, depth, mipLevels, format, surfaces, 1.0f, 0.0f);
    }

    public static Texture ofCube(int width, int height, int mipLevels, TextureFormat format, List<Surface> surfaces) {
        return new Texture(TextureKind.CUBE_MAP, width, height, 6, mipLevels, format, surfaces, 1.0f, 0.0f);
    }

    public static Texture fromSurface(Surface surface, TextureFormat format) {
        return fromSurface(surface, format, 1.0f, 0.0f);
    }

    public static Texture fromSurface(Surface surface, TextureFormat format, float scale, float bias) {
        return new Texture(
            TextureKind.TEXTURE_2D,
            surface.width(),
            surface.height(),
            1,
            1,
            format,
            List.of(surface),
            scale,
            bias
        );
    }
}
