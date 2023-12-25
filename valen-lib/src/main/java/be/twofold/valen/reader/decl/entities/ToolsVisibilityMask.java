package be.twofold.valen.reader.decl.entities;

import be.twofold.valen.reader.decl.*;

public enum ToolsVisibilityMask implements NamedEnum {
    AlwaysVisible("ALWAYS_VISIBLE", 0x0),
    IdStudio("IDSTUDIO", 0x1),
    IdStudioNonLit("IDSTUDIO_NON_LIT", 0x2),
    IdStudioOutline("IDSTUDIO_OUTLINE", 0x4),
    IdStudioOpaque("IDSTUDIO_OPAQUE", 0x8),
    IdStudioTranslucent("IDSTUDIO_TRANSLUCENT", 0x10),
    IdStudioCaulk("IDSTUDIO_CAULK", 0x20),
    IdStudioClip("IDSTUDIO_CLIP", 0x40),
    IdStudioXyOnly("IDSTUDIO_XY_ONLY", 0x80),
    IdStudioCameraOnly("IDSTUDIO_CAMERA_ONLY", 0x100),
    IdStudioHeightmapVolumeOnly("IDSTUDIO_HEIGHTMAPVOLUME_ONLY", 0x200);

    private final String name;
    private final int value;

    ToolsVisibilityMask(String name, int value) {
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
