package be.twofold.valen.core.game;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.texture.*;

public enum AssetType {
    TEXTURE(Texture.class, "Texture"),
    MODEL(Model.class, "Model"),
    BINARY(byte[].class, "Binary"),
    TEXT(String.class, "Text"),
    ;

    private final Class<?> type;
    private final String name;

    AssetType(Class<?> type, String name) {
        this.type = type;
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
