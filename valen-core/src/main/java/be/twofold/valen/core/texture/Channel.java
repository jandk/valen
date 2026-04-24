package be.twofold.valen.core.texture;

public enum Channel {
    RED(0),
    GREEN(1),
    BLUE(2),
    ALPHA(3),
    ;

    public final int index;

    Channel(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }
}
