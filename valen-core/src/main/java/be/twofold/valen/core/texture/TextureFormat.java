package be.twofold.valen.core.texture;

public record TextureFormat(
    BlockFormat blockFormat,
    NumericFormat numericFormat
) {
    public static final TextureFormat R8_UNORM = new TextureFormat(BlockFormat._8, NumericFormat.UNorm);
    public static final TextureFormat R8G8_UNORM = new TextureFormat(BlockFormat._16, NumericFormat.UNorm);
    public static final TextureFormat R8G8B8A8_UNORM = new TextureFormat(BlockFormat._32, NumericFormat.UNorm);
    public static final TextureFormat R16_SFLOAT = new TextureFormat(BlockFormat._16, NumericFormat.SFloat);
    public static final TextureFormat R16G16_SFLOAT = new TextureFormat(BlockFormat._32, NumericFormat.SFloat);

    public static final TextureFormat BC1_UNORM = new TextureFormat(BlockFormat.BC1, NumericFormat.UNorm);
    public static final TextureFormat BC1_SRGB = new TextureFormat(BlockFormat.BC1, NumericFormat.SRGB);
    public static final TextureFormat BC2_UNORM = new TextureFormat(BlockFormat.BC2, NumericFormat.UNorm);
    public static final TextureFormat BC2_SRGB = new TextureFormat(BlockFormat.BC2, NumericFormat.SRGB);
    public static final TextureFormat BC3_UNORM = new TextureFormat(BlockFormat.BC3, NumericFormat.UNorm);
    public static final TextureFormat BC3_SRGB = new TextureFormat(BlockFormat.BC3, NumericFormat.SRGB);
    public static final TextureFormat BC4_UNORM = new TextureFormat(BlockFormat.BC4, NumericFormat.UNorm);
    public static final TextureFormat BC4_SNORM = new TextureFormat(BlockFormat.BC4, NumericFormat.SNorm);
    public static final TextureFormat BC5_UNORM = new TextureFormat(BlockFormat.BC5, NumericFormat.UNorm);
    public static final TextureFormat BC5_SNORM = new TextureFormat(BlockFormat.BC5, NumericFormat.SNorm);
    public static final TextureFormat BC6H_UFLOAT = new TextureFormat(BlockFormat.BC6H, NumericFormat.UFloat);
    public static final TextureFormat BC6H_SFLOAT = new TextureFormat(BlockFormat.BC6H, NumericFormat.SFloat);
    public static final TextureFormat BC7_UNORM = new TextureFormat(BlockFormat.BC7, NumericFormat.UNorm);
    public static final TextureFormat BC7_SRGB = new TextureFormat(BlockFormat.BC7, NumericFormat.SRGB);
}
