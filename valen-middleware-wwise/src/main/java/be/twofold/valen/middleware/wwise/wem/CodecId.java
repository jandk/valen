package be.twofold.valen.middleware.wwise.wem;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public enum CodecId implements ValueEnum<Short> {
    PCM(0x0001),
    IMA_ADPCM(0x0002),
    OPUS(0x3040),
    WEM_OPUS(0x3041),
    PT_ADPCM(0x8311),
    PCM_EX(0xFFFE),
    VORBIS(0xFFFF),
    ;

    private final short value;

    CodecId(int value) {
        this.value = (short) value;
    }

    public static CodecId read(BinarySource source) throws IOException {
        short value = source.readShort();
        return ValueEnum.fromValue(CodecId.class, value);
    }

    @Override
    public Short value() {
        return value;
    }
}
