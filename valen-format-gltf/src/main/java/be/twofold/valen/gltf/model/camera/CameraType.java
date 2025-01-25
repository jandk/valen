package be.twofold.valen.gltf.model.camera;

import be.twofold.valen.gltf.model.*;

public enum CameraType implements SerializableEnum<String> {
    PERSPECTIVE("perspective"),
    ORTHOGRAPHIC("orthographic");

    private final String value;

    CameraType(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
