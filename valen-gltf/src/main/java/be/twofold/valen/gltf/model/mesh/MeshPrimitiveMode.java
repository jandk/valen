package be.twofold.valen.gltf.model.mesh;

import be.twofold.valen.gltf.model.*;

public enum MeshPrimitiveMode implements ValueEnum<Integer> {
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

    @Override
    public Integer value() {
        return value;
    }
}
