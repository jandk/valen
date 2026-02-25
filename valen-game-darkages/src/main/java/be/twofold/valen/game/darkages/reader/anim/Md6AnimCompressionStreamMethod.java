package be.twofold.valen.game.darkages.reader.anim;

import wtf.reversed.toolbox.util.*;

public enum Md6AnimCompressionStreamMethod implements ValueEnum<Byte> {
    UNSTREAMED(0),
    UNSTREAMED_FIRST_FRAMESET(1),
    STREAMED(2),
    LODS(3),
    ;

    private final byte value;

    Md6AnimCompressionStreamMethod(int value) {
        this.value = (byte) value;
    }

    public static Md6AnimCompressionStreamMethod fromValue(byte value) {
        return ValueEnum.fromValue(Md6AnimCompressionStreamMethod.class, value);
    }

    @Override
    public Byte value() {
        return value;
    }
}
