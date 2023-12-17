package be.twofold.valen.writer.gltf.model;

public enum AccessorType {
    SCALAR(1),
    VEC2(2),
    VEC3(3),
    VEC4(4),
    MAT2(4),
    MAT3(9),
    MAT4(16);

    private final int size;

    AccessorType(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
