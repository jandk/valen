package be.twofold.valen.game.idtech.defines;

public enum TextureMaterialKind {
    TMK_NONE,
    TMK_ALBEDO,
    TMK_SPECULAR,
    TMK_NORMAL,
    TMK_SMOOTHNESS,
    TMK_COVER,
    TMK_SSSMASK,
    TMK_COLORMASK,
    TMK_BLOOMMASK,
    TMK_HEIGHTMAP,
    TMK_DECALALBEDO,
    TMK_DECALNORMAL,
    TMK_DECALSPECULAR,
    TMK_LIGHTPROJECT,
    TMK_PARTICLE,
    TMK_LIGHTMAP,
    TMK_UI,
    TMK_FONT,
    TMK_LEGACY_FLASH_UI,
    TMK_LIGHTMAP_DIRECTIONAL,
    TMK_BLENDMASK,

    // Great Circle
    TMK_TINTMASK,
    TMK_TERRAIN_SPLATMAP,
    TMK_ECOTOPE_LAYER,
    TMK_DECALHEIGHTMAP,
    TMK_ALBEDO_UNSCALED,
    TMK_ALBEDO_DETAILS,

    // Dark Ages
    TMK_AO,
    TMK_PAINTEDDATAGRID,
    ;

    public static TextureMaterialKind parse(String textureMaterialKind) {
        return switch (textureMaterialKind.toLowerCase()) {
            case "albedo" -> TextureMaterialKind.TMK_ALBEDO;
            case "specular" -> TextureMaterialKind.TMK_SPECULAR;
            case "normal" -> TextureMaterialKind.TMK_NORMAL;
            case "smoothness" -> TextureMaterialKind.TMK_SMOOTHNESS;
            case "cover" -> TextureMaterialKind.TMK_COVER;
            case "colormask" -> TextureMaterialKind.TMK_COLORMASK;
            case "bloommask" -> TextureMaterialKind.TMK_BLOOMMASK;
            case "sssmask" -> TextureMaterialKind.TMK_SSSMASK;
            case "heightmap" -> TextureMaterialKind.TMK_HEIGHTMAP;
            case "decalalbedo" -> TextureMaterialKind.TMK_DECALALBEDO;
            case "decalnormal" -> TextureMaterialKind.TMK_DECALNORMAL;
            case "decalspecular" -> TextureMaterialKind.TMK_DECALSPECULAR;
            case "lightproject" -> TextureMaterialKind.TMK_LIGHTPROJECT;
            case "particle" -> TextureMaterialKind.TMK_PARTICLE;
            case "blendmask" -> TextureMaterialKind.TMK_BLENDMASK;
            case "ao" -> TextureMaterialKind.TMK_AO;
            default -> TextureMaterialKind.TMK_NONE;
        };
    }
}
