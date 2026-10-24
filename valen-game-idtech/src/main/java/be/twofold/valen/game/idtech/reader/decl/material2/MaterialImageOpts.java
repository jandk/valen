package be.twofold.valen.game.idtech.reader.decl.material2;

import be.twofold.valen.game.idtech.reader.decl.renderparm.*;
import be.twofold.valen.game.idtech.reader.image.*;
import com.google.gson.*;
import wtf.reversed.toolbox.util.*;

public record MaterialImageOpts(
    TextureType type,
    TextureFilter filter,
    TextureRepeat repeat,
    TextureFormat format,
    int atlasPadding,
    int minMip,
    int width3D,
    int height3D,
    int depth3D,
    boolean fullScaleBias,
    boolean noMips,
    boolean fftBloom,
    boolean prefilterMips
) {
    public MaterialImageOpts {
        Check.nonNull(type, "type");
        Check.nonNull(format, "format");
        Check.argument(atlasPadding >= 0 && atlasPadding <= 16, () -> "Invalid atlasPadding: " + atlasPadding);
        Check.argument(minMip >= -1, () -> "Invalid minMip: " + minMip);
        Check.positiveOrZero(width3D, "width3D");
        Check.positiveOrZero(height3D, "height3D");
        Check.positiveOrZero(depth3D, "depth3D");
        Check.argument(!(fftBloom && prefilterMips), "fftBloom and prefilterMips can't both be set");
    }

    public static MaterialImageOpts defaults(RenderParm renderParm) {
        var type = switch (renderParm.parmType()) {
            case PT_TEXTURE_3D -> TextureType.TT_3D;
            case PT_TEXTURE_CUBE -> TextureType.TT_CUBIC;
            default -> TextureType.TT_2D;
        };
        if (!(renderParm.declaredValue() instanceof ImageProperties image)) {
            return new MaterialImageOpts(
                type, null, null, TextureFormat.FMT_NONE,
                0, 0, 0, 0, 0,
                false, false, false, false
            );
        }

        var flags = image.flags();
        return new MaterialImageOpts(
            type, null, null, image.format(),
            image.padding(), 0, 0, 0, 0,
            flags.contains(ImageProperties.Flags.FULL_SCALE_BIAS),
            flags.contains(ImageProperties.Flags.NO_MIPS),
            flags.contains(ImageProperties.Flags.FFT_BLOOM),
            flags.contains(ImageProperties.Flags.PREFILTER_MIPS)
        );
    }

    public static MaterialImageOpts parse(JsonObject options, MaterialImageOpts defaults) {
        if (options == null) {
            return defaults;
        }

        var type = options.has("type") ? TextureType.valueOf(options.get("type").getAsString()) : defaults.type();
        var filter = options.has("filter") ? TextureFilter.valueOf(options.get("filter").getAsString()) : defaults.filter();
        var repeat = options.has("repeat") ? TextureRepeat.valueOf(options.get("repeat").getAsString()) : defaults.repeat();
        var format = options.has("format") ? TextureFormat.valueOf(options.get("format").getAsString()) : defaults.format();
        var atlasPadding = options.has("atlasPadding") ? options.get("atlasPadding").getAsInt() : defaults.atlasPadding();
        var minMip = options.has("minMip") ? options.get("minMip").getAsInt() : defaults.minMip();
        var width3D = options.has("width3D") ? options.get("width3D").getAsInt() : defaults.width3D();
        var height3D = options.has("height3D") ? options.get("height3D").getAsInt() : defaults.height3D();
        var depth3D = options.has("depth3D") ? options.get("depth3D").getAsInt() : defaults.depth3D();
        var fullScaleBias = options.has("fullScaleBias") ? options.get("fullScaleBias").getAsBoolean() : defaults.fullScaleBias();
        var noMips = options.has("noMips") ? options.get("noMips").getAsBoolean() : defaults.noMips();
        var fftBloom = options.has("fftBloom") ? options.get("fftBloom").getAsBoolean() : defaults.fftBloom();
        var prefilterMips = options.has("prefilterMips") ? options.get("prefilterMips").getAsBoolean() : defaults.prefilterMips();

        return new MaterialImageOpts(
            type, filter, repeat, format,
            atlasPadding, minMip, width3D, height3D, depth3D,
            fullScaleBias, noMips, fftBloom, prefilterMips
        );
    }
}
