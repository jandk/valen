package be.twofold.valen.gltf.model.image;

public enum ImageMimeType {
    ImageJpeg("image/jpeg"),
    ImagePng("image/png");

    private final String value;

    ImageMimeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
