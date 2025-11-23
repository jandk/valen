package be.twofold.valen.format.granite.gdex;

final class GdexInt64 extends Gdex {
    private final long value;

    GdexInt64(GdexItemTag tag, long value) {
        super(tag);
        this.value = value;
    }

    @Override
    public Long asNumber() {
        return value;
    }
}
