package be.twofold.valen.export.exr.model;

public enum PixelType {
    UINT(0, 4),
    HALF(1, 2),
    FLOAT(2, 4),
    ;

    private final int value;
    private final int size;

    PixelType(int value, int size) {
        this.value = value;
        this.size = size;
    }

    public int value() {
        return value;
    }

    public int size() {
        return size;
    }

}
