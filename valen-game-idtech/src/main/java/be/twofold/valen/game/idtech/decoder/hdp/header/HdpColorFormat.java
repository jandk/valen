package be.twofold.valen.game.idtech.decoder.hdp.header;

import wtf.reversed.toolbox.util.*;

/**
 * How a plane's channels are to be interpreted.
 */
public enum HdpColorFormat implements ValueEnum<Integer> {
    Y_ONLY(0),
    YUV_420(1),
    YUV_422(2),
    YUV_444(3),
    CMYK(4),
    BAYER(5),
    N_CHANNEL(6),
    ;

    private final int value;

    HdpColorFormat(int value) {
        this.value = value;
    }

    static HdpColorFormat fromValue(int value) {
        return ValueEnum.fromValue(HdpColorFormat.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
