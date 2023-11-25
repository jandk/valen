package be.twofold.valen.reader.image;

public enum ImageTextureType {
    TT_2D(0x00),
    TT_3D(0x01),
    TT_CUBIC(0x02);

    private static final ImageTextureType[] values = values();
    private final int code;

    ImageTextureType(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static ImageTextureType fromCode(int code) {
        for (ImageTextureType value : values) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
