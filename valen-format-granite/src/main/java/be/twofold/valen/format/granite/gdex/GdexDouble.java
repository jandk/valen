package be.twofold.valen.format.granite.gdex;

final class GdexDouble extends Gdex {
    private final double value;

    GdexDouble(GdexItemTag tag, double value) {
        super(tag);
        this.value = value;
    }
}
