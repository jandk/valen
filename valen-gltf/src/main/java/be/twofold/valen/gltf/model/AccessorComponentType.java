package be.twofold.valen.gltf.model;

public enum AccessorComponentType {
    SIGNED_BYTE(5120, 1),
    UNSIGNED_BYTE(5121, 1),
    SIGNED_SHORT(5122, 2),
    UNSIGNED_SHORT(5123, 2),
    UNSIGNED_INT(5125, 4),
    FLOAT(5126, 4);

    private final int id;
    private final int size;

    AccessorComponentType(int id, int size) {
        this.id = id;
        this.size = size;
    }

    public int id() {
        return id;
    }

    public int size() {
        return size;
    }
}
