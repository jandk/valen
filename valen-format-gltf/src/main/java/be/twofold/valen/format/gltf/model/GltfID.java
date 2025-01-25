package be.twofold.valen.format.gltf.model;

public abstract class GltfID {
    private final int id;

    protected GltfID(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null
            && getClass() == obj.getClass()
            && id == ((GltfID) obj).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }
}
