package be.twofold.valen.game.greatcircle;

import be.twofold.valen.game.greatcircle.reader.resources.*;
import be.twofold.valen.game.idtech.game.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public final class GreatCircleAssetID extends IdclAssetID<ResourcesVariation> {
    private static final Set<ResourcesVariation> NONE_SET = EnumSet.of(ResourcesVariation.RES_VAR_NONE);

    private static final Map<ResourceType, Set<ResourcesVariation>> VARIATIONS = Map.ofEntries(
        // Physics
        Map.entry(ResourceType.of("havokcompendium"), Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64)),
        Map.entry(ResourceType.of("havokshape"), Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64)),
        Map.entry(ResourceType.of("havokshapevpaint"), Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64)),
        Map.entry(ResourceType.of("hkcloth"), Set.of(ResourcesVariation.RES_VAR_PLATFORM_WIN64)),
        Map.entry(ResourceType.of("hknavmesh"), Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64)),
        Map.entry(ResourceType.of("hksoundnavvolume"), Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64)),
        Map.entry(ResourceType.of("ncg"), Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64)),

        // Logic
        Map.entry(ResourceType.of("logicentityres"), Set.of(ResourcesVariation.RES_VAR_RETAIL)),

        // Quality
        Map.entry(ResourceType.of("hair"), Set.of(
            ResourcesVariation.RES_VAR_QUALITY_HIGH,
            ResourcesVariation.RES_VAR_QUALITY_LOW
        )),

        // Language
        Map.entry(ResourceType.of("lang"), Set.of(
            ResourcesVariation.RES_VAR_LANGUAGE_AR_SA,
            ResourcesVariation.RES_VAR_LANGUAGE_DE_DE,
            ResourcesVariation.RES_VAR_LANGUAGE_EN_US,
            ResourcesVariation.RES_VAR_LANGUAGE_ES_ES,
            ResourcesVariation.RES_VAR_LANGUAGE_ES_MX,
            ResourcesVariation.RES_VAR_LANGUAGE_FR_FR,
            ResourcesVariation.RES_VAR_LANGUAGE_IT_IT,
            ResourcesVariation.RES_VAR_LANGUAGE_JA_JP,
            ResourcesVariation.RES_VAR_LANGUAGE_KO_KR,
            ResourcesVariation.RES_VAR_LANGUAGE_PL_PL,
            ResourcesVariation.RES_VAR_LANGUAGE_PT_BR,
            ResourcesVariation.RES_VAR_LANGUAGE_RU_RU,
            ResourcesVariation.RES_VAR_LANGUAGE_ZH_CN,
            ResourcesVariation.RES_VAR_LANGUAGE_ZH_TW
        )),
        Map.entry(ResourceType.of("lipsyncanim"), Set.of(
            ResourcesVariation.RES_VAR_LANGUAGE_DE_DE,
            ResourcesVariation.RES_VAR_LANGUAGE_EN_US,
            ResourcesVariation.RES_VAR_LANGUAGE_ES_ES,
            ResourcesVariation.RES_VAR_LANGUAGE_ES_MX,
            ResourcesVariation.RES_VAR_LANGUAGE_FR_FR,
            ResourcesVariation.RES_VAR_LANGUAGE_IT_IT,
            ResourcesVariation.RES_VAR_LANGUAGE_JA_JP,
            ResourcesVariation.RES_VAR_LANGUAGE_PL_PL,
            ResourcesVariation.RES_VAR_LANGUAGE_PT_BR
        )),

        // Shaders
        Map.entry(ResourceType.of("opacitymicromapdata"), Set.of(
            ResourcesVariation.RES_VAR_RENDERPROG_DUMMY,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_BASE
        )),
        Map.entry(ResourceType.of("renderprogresource"), Set.of(
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_AMD,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_AMD_RETAIL,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_BASE,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_BASE_RETAIL,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_NVIDIA,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_NVIDIA_RETAIL
        ))
    );

    GreatCircleAssetID(String name, ResourceType type, ResourcesVariation variation) {
        super(name, type, variation);
    }

    public static GreatCircleAssetID from(String name, ResourceType type) {
        var variations = VARIATIONS
            .getOrDefault(type, NONE_SET);

        Check.state(variations.size() == 1, "Multiple variations found");
        return new GreatCircleAssetID(name, type, variations.iterator().next());
    }

    public static GreatCircleAssetID material(String name) {
        return from(name, ResourceType.of("material2"));
    }
}
