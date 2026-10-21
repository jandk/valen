package be.twofold.valen.game.idtech.reader.soundcontainer;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public enum SoundEntryType implements ValueEnum<Short> {
    NONE((short) 0),
    OPUS((short) 2),
    WEM((short) 3),
    ;

    private final short value;

    SoundEntryType(short value) {
        this.value = value;
    }

    public static SoundEntryType read(BinarySource source) throws IOException {
        var value = source.readShort();
        return ValueEnum.fromValue(SoundEntryType.class, value);
    }

    @Override
    public Short value() {
        return value;
    }
}
