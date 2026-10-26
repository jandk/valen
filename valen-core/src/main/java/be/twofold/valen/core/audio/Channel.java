package be.twofold.valen.core.audio;

import java.util.*;
import java.util.stream.*;

public enum Channel {
    FRONT_LEFT("FL"),
    FRONT_RIGHT("FR"),
    FRONT_CENTER("FC"),
    LOW_FREQUENCY("LFE"),
    BACK_LEFT("BL"),
    BACK_RIGHT("BR"),
    FRONT_LEFT_OF_CENTER("FLC"),
    FRONT_RIGHT_OF_CENTER("FRC"),
    BACK_CENTER("BC"),
    SIDE_LEFT("SL"),
    SIDE_RIGHT("SR"),
    TOP_CENTER("TC"),
    TOP_FRONT_LEFT("TFL"),
    TOP_FRONT_CENTER("TFC"),
    TOP_FRONT_RIGHT("TFR"),
    TOP_BACK_LEFT("TBL"),
    TOP_BACK_CENTER("TBC"),
    TOP_BACK_RIGHT("TBR"),
    ;

    public static final List<Channel> VALUES = List.of(values());
    public static final List<Channel> MONO = List.of(FRONT_CENTER);
    public static final List<Channel> STEREO = List.of(FRONT_LEFT, FRONT_RIGHT);

    private final String shortName;

    Channel(String shortName) {
        this.shortName = shortName;
    }

    public static List<Channel> fromMask(int mask, int count) {
        if (Integer.bitCount(mask) == count && mask >>> VALUES.size() == 0) {
            return IntStream.range(0, VALUES.size())
                .filter(i -> (mask & (1 << i)) != 0)
                .mapToObj(VALUES::get)
                .toList();
        }

        if (count == 1) {
            return MONO;
        }
        if (count > VALUES.size()) {
            throw new UnsupportedOperationException("Unsupported channel count: " + count);
        }
        return VALUES.subList(0, count);
    }

    public String shortName() {
        return shortName;
    }
}
