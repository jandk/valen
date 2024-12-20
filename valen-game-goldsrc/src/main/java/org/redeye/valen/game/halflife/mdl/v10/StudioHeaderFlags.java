package org.redeye.valen.game.halflife.mdl.v10;

import java.util.*;

public enum StudioHeaderFlags {
    EF_ROCKET(1), // ! leave a trail
    EF_GRENADE(2), // ! leave a trail
    EF_GIB(4), // ! leave a trail
    EF_ROTATE(8), // ! rotate (bonus items)
    EF_TRACER(16), // ! green split trail
    EF_ZOMGIB(32), // ! small blood trail
    EF_TRACER2(64), // ! orange split trail + rotate
    EF_TRACER3(128), // ! purple trail
    EF_NOSHADELIGHT(256), // ! No shade lighting
    EF_HITBOXCOLLISIONS(512), // ! Use hitbox collisions
    EF_FORCESKYLIGHT(1024); // ! Forces the model to be lit by skybox lighting

    public final int bits;

    StudioHeaderFlags(int bits) {
        this.bits = bits;
    }

    public static Set<StudioHeaderFlags> fromCode(byte[] code) {
        var bits = BitSet.valueOf(code);
        var flags = EnumSet.noneOf(StudioHeaderFlags.class);
        for (var value : StudioHeaderFlags.values()) {
            if (bits.get(value.bits)) {
                flags.add(value);
            }
        }
        return flags;
    }
}
