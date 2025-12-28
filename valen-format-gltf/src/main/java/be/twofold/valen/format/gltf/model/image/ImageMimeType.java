package be.twofold.valen.format.gltf.model.image;

import be.twofold.valen.format.gltf.model.*;

public enum ImageMimeType implements SerializableEnum<String> {
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png");

    private final String value;

    ImageMimeType(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
