package be.twofold.valen.gltf.model.image;

import be.twofold.valen.gltf.model.*;

public enum ImageMimeType implements ValueEnum<String> {
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
