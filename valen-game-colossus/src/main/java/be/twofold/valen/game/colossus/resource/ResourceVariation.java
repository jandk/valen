package be.twofold.valen.game.colossus.resource;

import be.twofold.valen.core.util.*;

public enum ResourceVariation implements ValueEnum<Integer> {
    None(0),
    RenderProgOglPc(32),
    RenderProgVulkanPcBase(33),
    RenderProgVulkanPcAmd(34),
    RenderProgVulkanPcNvidia(35),
    RenderProgVulkanPcIntel(36),
    RenderProgD3dPc(37),
    RenderProgXb1BaseD3d11(38),
    RenderProgXb1ScorpioD3d11(39),
    RenderProgPs4Base(40),
    RenderProgPs4Neo(41),
    RenderProgPs4BaseSrt(42),
    RenderProgPs4NeoSrt(43),
    RenderProgXb1Base(44),
    RenderProgXb1Scorpio(45),
    RenderProgVulkanPcAmd16(46),
    RenderProgVulkanPcAmdRpm(47),
    RenderProgVulkanPcNvidiaTuring(49),
    HkGen64(64),
    HkMsvc64(65),
    PlatformWin(200),
    PlatformWin32(201),
    PlatformWin64(202),
    PlatformLinux(210),
    PlatformLinux32(211),
    PlatformLinux64(212),
    PlatformPs4(220),
    PlatformPs4Neo(221),
    PlatformXb1(230),
    PlatformXb1Scorpio(231),
    PlatformReservedDoNotUse(299);

    private final int value;

    ResourceVariation(int value) {
        this.value = value;
    }

    public static ResourceVariation fromValue(int value) {
        return ValueEnum.fromValue(ResourceVariation.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
