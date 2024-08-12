package be.twofold.valen.game.eternal.resource;

import be.twofold.valen.core.util.*;

import java.util.*;

public enum ResourceVariation implements ValueEnum<Integer> {
    None(0),
    RenderProgVulkanPcBase(33),
    RenderProgVulkanPcAmd(34),
    RenderProgD3d12PcBase(37),
    RenderProgPs4BaseSrt(42),
    RenderProgPs4NeoSrt(43),
    RenderProgXb1BaseD3d12(44),
    RenderProgSwitch(48),
    RenderProgVulkanYeti(49),
    RenderProgScarlettD3d12(50),
    RenderProgProspero(51),
    RenderProgVulkanPcBaseRetail(300),
    RenderProgVulkanPcAmdRetail(301),
    RenderProgD3d12PcBaseRetail(304),
    RenderProgPs4BaseSrtRetail(305),
    RenderProgPs4NeoSrtRetail(306),
    RenderProgXb1BaseD3d12Retail(307),
    RenderProgSwitchRetail(311),
    RenderProgVulkanYetiRetail(312),
    RenderProgScarlettD3d12Retail(313),
    RenderProgProsperoRetail(314),
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
    PlatformSwitch(240),
    PlatformScarlett(241),
    PlatformProspero(242),
    PlatformReservedDoNotUse(299);

    private static final Map<Integer, ResourceVariation> MAP = ValueEnum.valueMap(ResourceVariation.class);

    private final int value;

    ResourceVariation(int value) {
        this.value = value;
    }

    @Override
    public Integer value() {
        return value;
    }

    public static ResourceVariation fromValue(int value) {
        return ValueEnum.fromValue(MAP, value)
            .orElseThrow(() -> new IllegalArgumentException("Unknown variation: " + value));
    }
}
