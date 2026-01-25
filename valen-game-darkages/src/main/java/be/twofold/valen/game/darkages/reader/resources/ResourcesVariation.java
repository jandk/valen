package be.twofold.valen.game.darkages.reader.resources;

import wtf.reversed.toolbox.util.*;

public enum ResourcesVariation implements ValueEnum<Integer> {
    RES_VAR_NONE(0),
    RES_VAR_RENDERPROG_VULKAN_PC_BASE(33),
    RES_VAR_RENDERPROG_VULKAN_PC_AMD(34),
    RES_VAR_RENDERPROG_SCARLETT_D3D12(50),
    RES_VAR_RENDERPROG_PROSPERO_BASE(51),
    RES_VAR_RENDERPROG_PROSPERO_TRINITY(52),
    RES_VAR_RENDERPROG_VULKAN_PC_BASE_RETAIL(300),
    RES_VAR_RENDERPROG_VULKAN_PC_AMD_RETAIL(301),
    RES_VAR_RENDERPROG_SCARLETT_D3D12_RETAIL(313),
    RES_VAR_RENDERPROG_PROSPERO_BASE_RETAIL(314),
    RES_VAR_RENDERPROG_PROSPERO_TRINITY_RETAIL(315),
    RES_VAR_HK_GEN_64(64),
    RES_VAR_HK_MSVC_64(65),
    RES_VAR_DECLS_NO_SANITYCHECKS(70),
    RES_VAR_PLATFORM_WIN(200),
    RES_VAR_PLATFORM_WIN32(201),
    RES_VAR_PLATFORM_WIN64(202),
    RES_VAR_PLATFORM_LINUX(210),
    RES_VAR_PLATFORM_LINUX32(211),
    RES_VAR_PLATFORM_LINUX64(212),
    RES_VAR_PLATFORM_SCARLETT(241),
    RES_VAR_PLATFORM_PROSPERO(242),
    RES_VAR_PLATFORM_PROSPERO_BASE(243),
    RES_VAR_PLATFORM_PROSPERO_TRINITY(244),
    RES_VAR_PLATFORM_RESERVED_DO_NOT_USE(299),
    ;

    private final int value;

    ResourcesVariation(int value) {
        this.value = value;
    }

    public static ResourcesVariation fromValue(int value) {
        return ValueEnum.fromValue(ResourcesVariation.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
