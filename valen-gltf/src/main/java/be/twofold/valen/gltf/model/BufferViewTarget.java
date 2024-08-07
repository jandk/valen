package be.twofold.valen.gltf.model;

public enum BufferViewTarget {
    ARRAY_BUFFER(34962),
    ELEMENT_ARRAY_BUFFER(34963);

    private final int id;

    BufferViewTarget(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
