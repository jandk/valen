package be.twofold.valen.gltf.model;

public enum MeshPrimitiveMode {
    POINTS(0),
    LINES(1),
    LINE_LOOP(2),
    LINE_STRIP(3),
    TRIANGLES(4),
    TRIANGLE_STRIP(5),
    TRIANGLE_FAN(6);

    private final int value;

    MeshPrimitiveMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
