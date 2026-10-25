package be.twofold.valen.export.gltf;

public enum GltfExportMode {
    GLB("glb", "glTF as .glb (single file)"),
    GLTF_SPLIT("gltf", "glTF as .gltf, .bin and images"),
    ;

    private final String extension;
    private final String displayName;

    GltfExportMode(String extension, String displayName) {
        this.extension = extension;
        this.displayName = displayName;
    }

    public String extension() {
        return extension;
    }

    public String displayName() {
        return displayName;
    }
}
