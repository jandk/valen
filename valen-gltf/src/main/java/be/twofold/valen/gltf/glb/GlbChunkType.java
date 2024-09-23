package be.twofold.valen.gltf.glb;

public enum GlbChunkType {
    JSON(0x4E4F534A),
    BIN(0x004E4942);

    private final int value;

    GlbChunkType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
