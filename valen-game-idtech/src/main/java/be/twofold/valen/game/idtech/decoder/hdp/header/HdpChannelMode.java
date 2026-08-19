package be.twofold.valen.game.idtech.decoder.hdp.header;

import wtf.reversed.toolbox.util.*;

/**
 * Whether a band's quantizers are shared across channels or given per channel.
 */
public enum HdpChannelMode implements ValueEnum<Integer> {
    UNIFORM(0),
    SEPARATE(1),
    INDEPENDENT(2),
    ;

    private final int value;

    HdpChannelMode(int value) {
        this.value = value;
    }

    static HdpChannelMode fromValue(int value) {
        return ValueEnum.fromValue(HdpChannelMode.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
