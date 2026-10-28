package be.twofold.valen.game.colossus.reader.resources;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public enum ResourcesVariation implements ValueEnum<Integer> {
    RES_VAR_NONE(0),
    RES_VAR_RENDERPROG_OGL_PC(32),
    RES_VAR_RENDERPROG_VULKAN_PC_BASE(33),
    RES_VAR_RENDERPROG_VULKAN_PC_AMD(34),
    RES_VAR_RENDERPROG_VULKAN_PC_NVIDIA(35),
    RES_VAR_RENDERPROG_VULKAN_PC_INTEL(36),
    RES_VAR_RENDERPROG_D3D_PC(37),
    RES_VAR_RENDERPROG_XB1_BASE_D3D11(38),
    RES_VAR_RENDERPROG_XB1_SCORPIO_D3D11(39),
    RES_VAR_RENDERPROG_PS4_BASE(40),
    RES_VAR_RENDERPROG_PS4_NEO(41),
    RES_VAR_RENDERPROG_PS4_BASE_SRT(42),
    RES_VAR_RENDERPROG_PS4_NEO_SRT(43),
    RES_VAR_RENDERPROG_XB1_BASE(44),
    RES_VAR_RENDERPROG_XB1_SCORPIO(45),
    RES_VAR_RENDERPROG_VULKAN_PC_AMD16(46),
    RES_VAR_RENDERPROG_VULKAN_PC_AMD_RPM(47),
    RES_VAR_RENDERPROG_VULKAN_PC_NVIDIA_TURING(49),
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
    RES_VAR_PLATFORM_RESERVED_DO_NOT_USE(299),
    ;

    private final int value;
    private final String displayName;

    ResourcesVariation(int value) {
        this.value = value;
        this.displayName = name().replace("RES_VAR_", "");
    }

    public static ResourcesVariation read(BinarySource source) throws IOException {
        return ValueEnum.fromValue(ResourcesVariation.class, Short.toUnsignedInt(source.readShort()));
    }

    @Override
    public Integer value() {
        return value;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
