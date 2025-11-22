package be.twofold.valen.format.granite.gdex;

final class GdexInt32 extends Gdex {
    private final int value;

    GdexInt32(GdexItemTag tag, int value) {
        super(tag);
        this.value = value;
    }
}
