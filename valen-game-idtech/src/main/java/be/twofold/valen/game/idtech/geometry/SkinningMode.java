package be.twofold.valen.game.idtech.geometry;

public enum SkinningMode {
    None(0),
    Fixed4(4),
    Skinning1(1),
    Skinning4(4),
    Skinning6(6),
    Skinning8(8);

    private final int influence;

    SkinningMode(int influence) {
        this.influence = influence;
    }

    public int influence() {
        return influence;
    }
}
