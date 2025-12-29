package be.twofold.valen.game.goldsrc.reader.mdl.v10;

import wtf.reversed.toolbox.util.*;

import java.util.*;

public enum StudioHeaderFlags implements FlagEnum {
    /**
     * leave a trail
     */
    EF_ROCKET(1 << 0),
    /**
     * leave a trail
     */
    EF_GRENADE(1 << 1),
    /**
     * leave a trail
     */
    EF_GIB(1 << 2),
    /**
     * rotate (bonus items)
     */
    EF_ROTATE(1 << 3),
    /**
     * green split trail
     */
    EF_TRACER(1 << 4),
    /**
     * small blood trail
     */
    EF_ZOMGIB(1 << 5),
    /**
     * orange split trail + rotate
     */
    EF_TRACER2(1 << 6),
    /**
     * purple trail
     */
    EF_TRACER3(1 << 7),
    /**
     * No shade lighting
     */
    EF_NOSHADELIGHT(1 << 8),
    /**
     * Use hitbox collisions
     */
    EF_HITBOXCOLLISIONS(1 << 9),
    /**
     * Forces the model to be lit by skybox lighting
     */
    EF_FORCESKYLIGHT(1 << 10);

    public final int value;

    StudioHeaderFlags(int value) {
        this.value = value;
    }

    public static Set<StudioHeaderFlags> fromValue(int value) {
        return FlagEnum.fromValue(StudioHeaderFlags.class, value);
    }

    @Override
    public int value() {
        return value;
    }
}
