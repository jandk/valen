package be.twofold.valen.format.png;

import java.util.zip.*;

public enum PngCompression {
    NONE(PngFilterMethod.NONE, Deflater.NO_COMPRESSION),
    FASTEST(PngFilterMethod.UP, Deflater.BEST_SPEED),
    FAST(PngFilterMethod.ADAPTIVE, Deflater.BEST_SPEED),
    BALANCED(PngFilterMethod.ADAPTIVE, Deflater.DEFAULT_COMPRESSION),
    BEST(PngFilterMethod.ADAPTIVE, Deflater.BEST_COMPRESSION),
    ;

    private final PngFilterMethod filterMethod;
    private final int deflateLevel;

    PngCompression(PngFilterMethod filterMethod, int deflateLevel) {
        this.filterMethod = filterMethod;
        this.deflateLevel = deflateLevel;
    }

    public PngFilterMethod filterMethod() {
        return filterMethod;
    }

    public int deflateLevel() {
        return deflateLevel;
    }
}
