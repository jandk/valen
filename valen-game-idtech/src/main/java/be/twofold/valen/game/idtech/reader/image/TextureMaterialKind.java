package be.twofold.valen.game.idtech.reader.image;

import java.util.*;

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
        return switch (textureMaterialKind.toLowerCase(Locale.ROOT)) {
            case "albedo" -> TextureMaterialKind.TMK_ALBEDO;
            case "albedodetails" -> TextureMaterialKind.TMK_ALBEDO_DETAILS;
            case "albedounscaled" -> TextureMaterialKind.TMK_ALBEDO_UNSCALED;
            case "ao" -> TextureMaterialKind.TMK_AO;
            case "blendmask" -> TextureMaterialKind.TMK_BLENDMASK;
            case "bloommask" -> TextureMaterialKind.TMK_BLOOMMASK;
            case "colormask" -> TextureMaterialKind.TMK_COLORMASK;
            case "cover" -> TextureMaterialKind.TMK_COVER;
            case "decalalbedo" -> TextureMaterialKind.TMK_DECALALBEDO;
            case "decalheightmap" -> TextureMaterialKind.TMK_DECALHEIGHTMAP;
            case "decalnormal" -> TextureMaterialKind.TMK_DECALNORMAL;
            case "decalspecular" -> TextureMaterialKind.TMK_DECALSPECULAR;
            case "font" -> TextureMaterialKind.TMK_FONT;
            case "heightmap" -> TextureMaterialKind.TMK_HEIGHTMAP;
            case "legacyflashui" -> TextureMaterialKind.TMK_LEGACY_FLASH_UI;
            case "lightmap" -> TextureMaterialKind.TMK_LIGHTMAP;
            case "lightmapdir" -> TextureMaterialKind.TMK_LIGHTMAP_DIRECTIONAL;
            case "lightproject" -> TextureMaterialKind.TMK_LIGHTPROJECT;
            case "normal" -> TextureMaterialKind.TMK_NORMAL;
            case "particle" -> TextureMaterialKind.TMK_PARTICLE;
            case "smoothness" -> TextureMaterialKind.TMK_SMOOTHNESS;
            case "specular" -> TextureMaterialKind.TMK_SPECULAR;
            case "sssmask" -> TextureMaterialKind.TMK_SSSMASK;
            case "tintmask" -> TextureMaterialKind.TMK_TINTMASK;
            case "ui" -> TextureMaterialKind.TMK_UI;
            default -> TextureMaterialKind.TMK_NONE;
        };
    }
}
