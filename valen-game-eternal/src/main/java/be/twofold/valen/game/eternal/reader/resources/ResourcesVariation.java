package be.twofold.valen.game.eternal.reader.resources;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public enum ResourcesVariation implements ValueEnum<Integer> {
    RES_VAR_NONE(0),
    RES_VAR_RENDERPROG_VULKAN_PC_BASE(33),
    RES_VAR_RENDERPROG_VULKAN_PC_AMD(34),
    RES_VAR_RENDERPROG_D3D12_PC_BASE(37),
    RES_VAR_RENDERPROG_PS4_BASE_SRT(42),
    RES_VAR_RENDERPROG_PS4_NEO_SRT(43),
    RES_VAR_RENDERPROG_XB1_BASE_D3D12(44),
    RES_VAR_RENDERPROG_SWITCH(48),
    RES_VAR_RENDERPROG_SCARLETT_D3D12(50),
    RES_VAR_RENDERPROG_PROSPERO(51),
    RES_VAR_RENDERPROG_VULKAN_PC_BASE_RETAIL(300),
    RES_VAR_RENDERPROG_VULKAN_PC_AMD_RETAIL(301),
    RES_VAR_RENDERPROG_D3D12_PC_BASE_RETAIL(304),
    RES_VAR_RENDERPROG_PS4_BASE_SRT_RETAIL(305),
    RES_VAR_RENDERPROG_PS4_NEO_SRT_RETAIL(306),
    RES_VAR_RENDERPROG_XB1_BASE_D3D12_RETAIL(307),
    RES_VAR_RENDERPROG_SWITCH_RETAIL(311),
    RES_VAR_RENDERPROG_SCARLETT_D3D12_RETAIL(313),
    RES_VAR_RENDERPROG_PROSPERO_RETAIL(314),
    RES_VAR_HK_GEN_64(64),
    RES_VAR_HK_MSVC_64(65),
    RES_VAR_PLATFORM_WIN(200),
    RES_VAR_PLATFORM_WIN32(201),
    RES_VAR_PLATFORM_WIN64(202),
    RES_VAR_PLATFORM_LINUX(210),
    RES_VAR_PLATFORM_LINUX32(211),
    RES_VAR_PLATFORM_LINUX64(212),
    RES_VAR_PLATFORM_PS4(220),
    RES_VAR_PLATFORM_PS4_NEO(221),
    RES_VAR_PLATFORM_XB1(230),
    RES_VAR_PLATFORM_XB1_SCORPIO(231),
    RES_VAR_PLATFORM_SWITCH(240),
    RES_VAR_PLATFORM_SCARLETT(241),
    RES_VAR_PLATFORM_PROSPERO(242),
    RES_VAR_PLATFORM_RESERVED_DO_NOT_USE(299),
    ;

    private final int value;

    ResourcesVariation(int value) {
        this.value = value;
    }

    public static ResourcesVariation read(BinarySource source) throws IOException {
        return ValueEnum.fromValue(ResourcesVariation.class, Short.toUnsignedInt(source.readShort()));
    }

    @Override
    public Integer value() {
        return value;
    }
}
