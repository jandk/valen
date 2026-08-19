package be.twofold.valen.game.idtech.decoder.hdp.header;

import wtf.reversed.toolbox.util.*;

/**
 * Which frequency bands the encoder kept in the stream.
 */
public enum HdpSubband implements ValueEnum<Integer> {
    SB_ALL(0),
    SB_NO_FLEXBITS(1),
    SB_NO_HIGHPASS(2),
    SB_DC_ONLY(3),
    SB_ISOLATED(4),
    ;

    private final int value;

    HdpSubband(int value) {
        this.value = value;
    }

    static HdpSubband fromValue(int value) {
        return ValueEnum.fromValue(HdpSubband.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
