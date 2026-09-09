package be.twofold.valen.middleware.wwise.wem;

import wtf.reversed.toolbox.util.*;

public record Wem(
    WaveFormat format,
    int dataOffset,
    int dataSize,
    boolean truncated
) {
    public Wem {
        Check.nonNull(format, "format");
        Check.positive(dataOffset, "dataOffset");
        Check.positive(dataSize, "dataSize");
    }
}
