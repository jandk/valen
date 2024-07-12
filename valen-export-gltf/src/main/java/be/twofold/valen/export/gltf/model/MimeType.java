package be.twofold.valen.export.gltf.model;

public enum MimeType {
    ImageJpeg("image/jpeg"),
    ImagePng("image/png");

    private final String value;

    MimeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
