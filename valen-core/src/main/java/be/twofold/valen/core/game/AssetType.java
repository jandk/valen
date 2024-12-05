package be.twofold.valen.core.game;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.texture.*;

public final class AssetType<T> {
    public static final AssetType<byte[]> BINARY = new AssetType<>(byte[].class, "Binary");
    public static final AssetType<Texture> TEXTURE = new AssetType<>(Texture.class, "Texture");
    public static final AssetType<Model> MODEL = new AssetType<>(Model.class, "Model");
    public static final AssetType<String> TEXT = new AssetType<>(String.class, "Text");
    private final Class<T> clazz;
    private final String name;

    private AssetType(Class<T> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    public Class<T> clazz() {
        return clazz;
    }

    public String name() {
        return name;
    }
}
