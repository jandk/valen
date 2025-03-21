package be.twofold.valen.game.greatcircle.reader.decl.material2;

public enum ImageTextureFilter {
    TF_LINEAR(0),
    TF_NEAREST(1),
    TF_MIN(2),
    TF_MAX(3),
    TF_NEAREST_MIPMAP_NEAREST(4),
    TF_LINEAR_MIPMAP_NEAREST(5),
    TF_TRILINEAR(6),
    TF_DEFAULT(7);

    private final int code;

    ImageTextureFilter(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
