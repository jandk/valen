package be.twofold.valen.core.game;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.texture.*;
import wtf.reversed.toolbox.collect.*;

public enum AssetType {
    ANIMATION(Animation.class, "Animation"),
    MATERIAL(Material.class, "Material"),
    MODEL(Model.class, "Model"),
    TEXTURE(Texture.class, "Texture"),
    RAW(Bytes.class, "Raw"),
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
