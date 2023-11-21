package be.twofold.valen.writer.gltf.model;

import com.fasterxml.jackson.annotation.*;

public enum BufferViewTarget {
    ARRAY_BUFFER(34962),
    ELEMENT_ARRAY_BUFFER(34963);

    private final int id;

    BufferViewTarget(int id) {
        this.id = id;
    }

    @JsonValue
    public int getId() {
        return id;
    }
}
