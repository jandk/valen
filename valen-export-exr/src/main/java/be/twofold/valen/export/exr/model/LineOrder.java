package be.twofold.valen.export.exr.model;

public enum LineOrder {
    INCREASING_Y(0),
    DECREASING_Y(1),
    RANDOM_Y(2),
    ;

    private final int value;

    LineOrder(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
