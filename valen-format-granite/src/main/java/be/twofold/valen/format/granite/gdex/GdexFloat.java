package be.twofold.valen.format.granite.gdex;

final class GdexFloat extends Gdex {
    private final float value;

    GdexFloat(GdexItemTag tag, float value) {
        super(tag);
        this.value = value;
    }
}
