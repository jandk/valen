package be.twofold.valen.format.gltf.model.mesh;

import be.twofold.valen.format.gltf.model.*;

public enum MeshPrimitiveMode implements SerializableEnum<Integer> {
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
        return 0;
    }
}
