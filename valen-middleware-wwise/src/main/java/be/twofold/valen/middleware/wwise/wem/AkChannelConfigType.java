package be.twofold.valen.middleware.wwise.wem;

import wtf.reversed.toolbox.util.*;

public enum AkChannelConfigType implements ValueEnum<Integer> {
    ANONYMOUS(0),
    STANDARD(1),
    AMBISONIC(2),
    OBJECTS(3),
    ;

    private final int value;

    AkChannelConfigType(int value) {
        this.value = value;
    }

    public static AkChannelConfigType fromValue(int value) {
        return ValueEnum.fromValue(AkChannelConfigType.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
