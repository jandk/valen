package be.twofold.valen.export.exr.model;

import be.twofold.valen.core.util.*;

public record Channel(
    String name,
    PixelType pixelType,
    boolean pLinear,
    int xSampling,
    int ySampling
) {
    public Channel {
        Check.notNull(name, "name");
        Check.notNull(pixelType, "pixelType");
    }
}
