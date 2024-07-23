package be.twofold.valen.gltf.glb;

public enum GlbChunkType {
    Json(0x4e4f534a),
    Bin(0x004e4942);

    private final int value;

    GlbChunkType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
