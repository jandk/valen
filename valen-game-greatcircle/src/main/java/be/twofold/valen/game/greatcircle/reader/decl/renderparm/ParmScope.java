package be.twofold.valen.game.greatcircle.reader.decl.renderparm;

public enum ParmScope {
    PSCP_VIEW(0),
    PSCP_INSTANCE(1),
    PSCP_SURFACE(2);

    private static final ParmScope[] values = values();
    private final int value;

    ParmScope(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
