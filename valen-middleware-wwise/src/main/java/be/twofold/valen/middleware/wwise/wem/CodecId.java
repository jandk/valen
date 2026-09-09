package be.twofold.valen.middleware.wwise.wem;

import wtf.reversed.toolbox.util.*;

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

    public static CodecId fromValue(short value) {
        return ValueEnum.fromValue(CodecId.class, value);
    }

    @Override
    public Short value() {
        return value;
    }
}
