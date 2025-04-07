package be.twofold.valen.export.exr.model;

public enum Compression {
    NO_COMPRESSION(0, 1),
    RLE_COMPRESSION(1, 1),
    ZIPS_COMPRESSION(2, 1),
    ZIP_COMPRESSION(3, 16),
    PIZ_COMPRESSION(4, 32),
    PXR24_COMPRESSION(5, 16),
    B44_COMPRESSION(6, 32),
    B44A_COMPRESSION(7, 32),
    DWAA_COMPRESSION(8, 32),
    DWAB_COMPRESSION(9, 256),
    ;

    private final int value;
    private final int linesPerBlock;

    Compression(int value, int linesPerBlock) {
        this.value = value;
        this.linesPerBlock = linesPerBlock;
    }

    public int value() {
        return value;
    }

    public int linesPerBlock() {
        return linesPerBlock;
    }
}
