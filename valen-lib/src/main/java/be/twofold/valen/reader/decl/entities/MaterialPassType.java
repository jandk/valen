package be.twofold.valen.reader.decl.entities;

import be.twofold.valen.reader.decl.*;

public enum MaterialPassType implements NamedEnum {
    Opaque("MATERIALPASS_OPAQUE", 0),
    ZPrePass("MATERIALPASS_Z_PREPASS", 1),
    Geo_decals("MATERIALPASS_GEO_DECALS", 2),
    Shadow("MATERIALPASS_SHADOW", 3),
    Blend("MATERIALPASS_BLEND", 4),
    BlendEcho("MATERIALPASS_BLEND_ECHO", 5),
    BlendOpacity("MATERIALPASS_BLEND_OPACITY", 6),
    Gui("MATERIALPASS_GUI", 7),
    ParticleSurfaceSpawn("MATERIALPASS_PARTICLE_SURFACE_SPAWN", 8),
    ParticleLightAtlas("MATERIALPASS_PARTICLE_LIGHT_ATLAS", 9),
    Distortion("MATERIALPASS_DISTORTION", 10),
    Flares("MATERIALPASS_FLARES", 11),
    DepthFixup("MATERIALPASS_DEPTHFIXUP", 12),
    GeoBlend("MATERIALPASS_GEOBLEND", 13),
    LateOpaque("MATERIALPASS_LATE_OPAQUE", 14),
    AfterPostprocess("MATERIALPASS_AFTER_POSTPROCESS", 15),
    Water("MATERIALPASS_WATER", 16),
    BlendGBuffer("MATERIALPASS_BLEND_GBUFFER", 17),
    RefractionMask("MATERIALPASS_REFRACTION_MASK", 18),
    WorldEditor("MATERIALPASS_WORLD_EDITOR", 19),
    WorldEditorBlend("MATERIALPASS_WORLD_EDITOR_BLEND", 20);

    private final String name;
    private final int value;

    MaterialPassType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}
