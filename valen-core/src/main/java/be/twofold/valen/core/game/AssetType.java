package be.twofold.valen.core.game;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.texture.*;
import wtf.reversed.toolbox.collect.*;

import java.util.*;
import java.util.stream.*;

public enum AssetType {
    ANIMATION(Animation.class, "Animation"),
    MATERIAL(Material.class, "Material"),
    MODEL(Model.class, "Model"),
    TEXTURE(Texture.class, "Texture"),
    RAW(Bytes.class, "Raw"),
    ;

    public static final Set<AssetType> ALL_NO_RAW = Arrays.stream(values())
        .filter(type -> type != RAW)
        .collect(Collectors.toUnmodifiableSet());

    private final Class<?> type;
    private final String displayName;

    AssetType(Class<?> type, String displayName) {
        this.type = type;
        this.displayName = displayName;
    }

    public Class<?> type() {
        return type;
    }

    public String displayName() {
        return displayName;
    }
}
