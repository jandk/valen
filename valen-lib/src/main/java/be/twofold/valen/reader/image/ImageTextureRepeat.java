package be.twofold.valen.reader.image;

public enum ImageTextureRepeat {
    TR_REPEAT(0),
    TR_CLAMP(1),
    TR_CLAMP_S(2),
    TR_CLAMP_T(3),
    TR_CLAMP_TO_BORDER(4),
    TR_MIRROR(5);

    private final int code;

    ImageTextureRepeat(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
